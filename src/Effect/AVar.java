    // Port of Effect/AVar.js: an AVar holds at most one value, waiters queue
    // up for takes, reads and puts, and a kill notifies every waiter.
    private static final Object __EMPTY = new Object();

    public static final class AVarCell {
        Object value = __EMPTY;
        Object error = null;
        int draining = 0;
        final java.util.ArrayDeque<java.util.function.Function<Object, Object>> takes = new java.util.ArrayDeque<>();
        final java.util.ArrayDeque<java.util.function.Function<Object, Object>> reads = new java.util.ArrayDeque<>();
        final java.util.ArrayDeque<Object[]> puts = new java.util.ArrayDeque<>();
    }

    private static void __runCallback(java.util.function.Function<Object, Object> callback, Object either) {
        if (callback == null) return;
        Object effect = callback.apply(either);
        if (effect instanceof java.util.function.Supplier) ((java.util.function.Supplier<Object>) effect).get();
    }

    private static void __notifyReaders(AVarCell cell, java.util.Map<String, Object> util, Object value) {
        java.util.function.Function<Object, Object> right = (java.util.function.Function<Object, Object>) util.get("right");
        while (!cell.reads.isEmpty()) __runCallback(cell.reads.poll(), right.apply(value));
    }

    private static void __drain(AVarCell cell, java.util.Map<String, Object> util) {
        java.util.function.Function<Object, Object> right = (java.util.function.Function<Object, Object>) util.get("right");
        if (cell.error != null) return;
        cell.draining++;
        try {
            while (true) {
                if (cell.value != __EMPTY) __notifyReaders(cell, util, cell.value);
                if (cell.value != __EMPTY && !cell.takes.isEmpty()) {
                    Object taken = cell.value;
                    cell.value = __EMPTY;
                    __runCallback(cell.takes.poll(), right.apply(taken));
                    continue;
                }
                if (cell.value == __EMPTY && !cell.puts.isEmpty()) {
                    Object[] put = cell.puts.poll();
                    java.util.function.Function<Object, Object> taker = cell.takes.poll();
                    __notifyReaders(cell, util, put[0]);
                    if (taker != null) {
                        __runCallback(taker, right.apply(put[0]));
                    } else {
                        cell.value = put[0];
                    }
                    __runCallback((java.util.function.Function<Object, Object>) put[1], right.apply(null));
                    continue;
                }
                return;
            }
        } finally {
            cell.draining--;
        }
    }

    // A value offered to a cell: waiting readers observe it, a waiting taker
    // consumes it, otherwise the cell is filled; the put callback runs last.
    private static boolean __offer(AVarCell cell, Object value, java.util.function.Function<Object, Object> callback, java.util.Map<String, Object> util) {
        java.util.function.Function<Object, Object> right = (java.util.function.Function<Object, Object>) util.get("right");
        java.util.function.Function<Object, Object> taker = cell.takes.poll();
        cell.draining++;
        try {
            __notifyReaders(cell, util, value);
            if (taker != null) {
                __runCallback(taker, right.apply(value));
            } else if (cell.value == __EMPTY) {
                cell.value = value;
            } else {
                return false;
            }
            if (callback != null) __runCallback(callback, right.apply(null));
        } finally {
            cell.draining--;
        }
        __drain(cell, util);
        return true;
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
                    while (!cell.reads.isEmpty()) __runCallback(cell.reads.poll(), left.apply(error));
                    while (!cell.takes.isEmpty()) __runCallback(cell.takes.poll(), left.apply(error));
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
                    __offer(cell, value, null, util);
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
                    if (cell.draining == 0) __drain(cell, util);
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
                    if (cell.draining == 0) __drain(cell, util);
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
                    if (cell.draining == 0) __drain(cell, util);
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
