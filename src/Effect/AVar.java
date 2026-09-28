    // Port of Effect/AVar.js: an AVar holds at most one value, waiters queue
    // up for takes, reads and puts, and a kill notifies every waiter.
    private static final Object __EMPTY = new Object();

    public static final class AVarCell {
        Object value = __EMPTY;
        Object error = null;
        final java.util.ArrayDeque<java.util.function.Function<Object, Object>> takes = new java.util.ArrayDeque<>();
        final java.util.ArrayDeque<java.util.function.Function<Object, Object>> reads = new java.util.ArrayDeque<>();
        final java.util.ArrayDeque<Object[]> puts = new java.util.ArrayDeque<>();
    }

    private static void __runCallback(java.util.function.Function<Object, Object> callback, Object either) {
        if (callback == null) return;
        Object effect = callback.apply(either);
        if (effect instanceof java.util.function.Supplier) ((java.util.function.Supplier<Object>) effect).get();
    }

    private static void __drain(AVarCell cell, java.util.Map<String, Object> util) {
        java.util.function.Function<Object, Object> right = (java.util.function.Function<Object, Object>) util.get("right");
        if (cell.error != null) return;
        while (cell.value != __EMPTY) {
            if (!cell.takes.isEmpty()) {
                java.util.function.Function<Object, Object> taker = cell.takes.poll();
                Object taken = cell.value;
                cell.value = __EMPTY;
                __runCallback(taker, right.apply(taken));
            } else if (!cell.reads.isEmpty()) {
                java.util.function.Function<Object, Object> reader = cell.reads.poll();
                __runCallback(reader, right.apply(cell.value));
                return;
            } else {
                return;
            }
        }
        while (!cell.puts.isEmpty()) {
            Object[] put = cell.puts.poll();
            if (!cell.takes.isEmpty()) {
                java.util.function.Function<Object, Object> taker = cell.takes.poll();
                __runCallback(taker, right.apply(put[0]));
                __runCallback((java.util.function.Function<Object, Object>) put[1], right.apply(null));
            } else {
                cell.value = put[0];
                __runCallback((java.util.function.Function<Object, Object>) put[1], right.apply(null));
                return;
            }
        }
    }

    private static AVarCell __avar(Object avar) { return (AVarCell) avar; }

    public static Object empty = (java.util.function.Supplier<Object>) () -> new AVarCell();

    public static Object _newVar = (java.util.function.Function<Object, Object>) (value) ->
        (java.util.function.Supplier<Object>) () -> {
            AVarCell cell = new AVarCell();
            cell.value = value;
            return cell;
        };

    public static Object _killVar = (java.util.function.Function<Object, Object>) (utilObj) ->
        (java.util.function.Function<Object, Object>) (error) ->
        (java.util.function.Function<Object, Object>) (avar) ->
            (java.util.function.Supplier<Object>) () -> {
                java.util.Map<String, Object> util = (java.util.Map<String, Object>) utilObj;
                java.util.function.Function<Object, Object> left = (java.util.function.Function<Object, Object>) util.get("left");
                AVarCell cell = __avar(avar);
                synchronized (cell) {
                    if (cell.error != null) return null;
                    cell.error = error;
                    while (!cell.takes.isEmpty()) __runCallback(cell.takes.poll(), left.apply(error));
                    while (!cell.reads.isEmpty()) __runCallback(cell.reads.poll(), left.apply(error));
                    while (!cell.puts.isEmpty()) {
                        Object[] put = cell.puts.poll();
                        __runCallback((java.util.function.Function<Object, Object>) put[1], left.apply(error));
                    }
                }
                return null;
            };

    public static Object _tryTakeVar = (java.util.function.Function<Object, Object>) (utilObj) ->
        (java.util.function.Function<Object, Object>) (avarObj) ->
            (java.util.function.Supplier<Object>) () -> {
                Object avar = avarObj;
                java.util.Map<String, Object> util = (java.util.Map<String, Object>) utilObj;
                AVarCell cell;
                synchronized (avar) {
                    cell = __avar(avar);
                    if (cell.error != null) throw __asError(cell.error);
                    if (cell.value == __EMPTY) return util.get("nothing");
                    Object taken = cell.value;
                    cell.value = __EMPTY;
                    __drain(cell, util);
                    return ((java.util.function.Function<Object, Object>) util.get("just")).apply(taken);
                }
            };

    public static Object _tryReadVar = (java.util.function.Function<Object, Object>) (utilObj) ->
        (java.util.function.Function<Object, Object>) (avarObj) ->
            (java.util.function.Supplier<Object>) () -> {
                Object avar = avarObj;
                java.util.Map<String, Object> util = (java.util.Map<String, Object>) utilObj;
                synchronized (avar) {
                    AVarCell cell = __avar(avar);
                    if (cell.error != null) throw __asError(cell.error);
                    return cell.value == __EMPTY
                        ? util.get("nothing")
                        : ((java.util.function.Function<Object, Object>) util.get("just")).apply(cell.value);
                }
            };

    public static Object _tryPutVar = (java.util.function.Function<Object, Object>) (utilObj) ->
        (java.util.function.Function<Object, Object>) (value) ->
        (java.util.function.Function<Object, Object>) (avarObj) ->
            (java.util.function.Supplier<Object>) () -> {
                Object avar = avarObj;
                java.util.Map<String, Object> util = (java.util.Map<String, Object>) utilObj;
                synchronized (avar) {
                    AVarCell cell = __avar(avar);
                    if (cell.error != null) throw __asError(cell.error);
                    if (cell.value != __EMPTY) return false;
                    if (!cell.takes.isEmpty()) {
                        java.util.function.Function<Object, Object> taker = cell.takes.poll();
                        __runCallback(taker, ((java.util.function.Function<Object, Object>) util.get("right")).apply(value));
                    } else {
                        cell.value = value;
                    }
                    __drain(cell, util);
                    return true;
                }
            };

    public static Object _takeVar = (java.util.function.Function<Object, Object>) (utilObj) ->
        (java.util.function.Function<Object, Object>) (avarObj) ->
        (java.util.function.Function<Object, Object>) (callback) ->
            (java.util.function.Supplier<Object>) () -> {
                Object avar = avarObj;
                java.util.Map<String, Object> util = (java.util.Map<String, Object>) utilObj;
                AVarCell cell;
                synchronized (avar) {
                    cell = __avar(avar);
                    if (cell.error != null) {
                        __runCallback((java.util.function.Function<Object, Object>) callback,
                            ((java.util.function.Function<Object, Object>) util.get("left")).apply(cell.error));
                        return (java.util.function.Supplier<Object>) () -> null;
                    }
                    cell.takes.add((java.util.function.Function<Object, Object>) callback);
                    __drain(cell, util);
                }
                return (java.util.function.Supplier<Object>) () -> {
                    synchronized (avar) { cell.takes.remove(callback); }
                    return null;
                };
            };

    public static Object _readVar = (java.util.function.Function<Object, Object>) (utilObj) ->
        (java.util.function.Function<Object, Object>) (avarObj) ->
        (java.util.function.Function<Object, Object>) (callback) ->
            (java.util.function.Supplier<Object>) () -> {
                Object avar = avarObj;
                java.util.Map<String, Object> util = (java.util.Map<String, Object>) utilObj;
                AVarCell cell;
                synchronized (avar) {
                    cell = __avar(avar);
                    if (cell.error != null) {
                        __runCallback((java.util.function.Function<Object, Object>) callback,
                            ((java.util.function.Function<Object, Object>) util.get("left")).apply(cell.error));
                        return (java.util.function.Supplier<Object>) () -> null;
                    }
                    cell.reads.add((java.util.function.Function<Object, Object>) callback);
                    __drain(cell, util);
                }
                return (java.util.function.Supplier<Object>) () -> {
                    synchronized (avar) { cell.reads.remove(callback); }
                    return null;
                };
            };

    public static Object _putVar = (java.util.function.Function<Object, Object>) (utilObj) ->
        (java.util.function.Function<Object, Object>) (value) ->
        (java.util.function.Function<Object, Object>) (avarObj) ->
        (java.util.function.Function<Object, Object>) (callback) ->
            (java.util.function.Supplier<Object>) () -> {
                Object avar = avarObj;
                java.util.Map<String, Object> util = (java.util.Map<String, Object>) utilObj;
                AVarCell cell;
                Object[] put = new Object[]{value, callback};
                synchronized (avar) {
                    cell = __avar(avar);
                    if (cell.error != null) {
                        __runCallback((java.util.function.Function<Object, Object>) callback,
                            ((java.util.function.Function<Object, Object>) util.get("left")).apply(cell.error));
                        return (java.util.function.Supplier<Object>) () -> null;
                    }
                    cell.puts.add(put);
                    __drain(cell, util);
                }
                return (java.util.function.Supplier<Object>) () -> {
                    synchronized (avar) { cell.puts.remove(put); }
                    return null;
                };
            };

    public static Object _status = (java.util.function.Function<Object, Object>) (utilObj) ->
        (java.util.function.Function<Object, Object>) (avarObj) ->
            (java.util.function.Supplier<Object>) () -> {
                java.util.Map<String, Object> util = (java.util.Map<String, Object>) utilObj;
                AVarCell cell = __avar(avarObj);
                synchronized (cell) {
                    if (cell.error != null) return ((java.util.function.Function<Object, Object>) util.get("killed")).apply(cell.error);
                    if (cell.value == __EMPTY) return util.get("empty");
                    return ((java.util.function.Function<Object, Object>) util.get("filled")).apply(cell.value);
                }
            };

    private static RuntimeException __asError(Object error) {
        return error instanceof RuntimeException ? (RuntimeException) error : new RuntimeException(String.valueOf(error));
    }
