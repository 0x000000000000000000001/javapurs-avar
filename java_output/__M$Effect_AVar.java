public class __M$Effect_AVar {
    public static final Object FFI_STUB = new java.util.function.Function<Object, Object>() {
        public Object apply(Object arg) { throw new UnsupportedOperationException("Missing Java FFI in Effect.AVar"); }
    };
    // FFI provided by src/Effect/AVar.java
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


public static final class Killed {
            public final Object value0;
            public Killed(Object value0){
                this.value0 = value0;
            }
        }
public static final class Filled {
            public final Object value0;
            public Filled(Object value0){
                this.value0 = value0;
            }
        }
public static final class Empty {
            
            public Empty(){
                
            }
        }
public static final class __singleton$Empty {
    public static final Empty value = new Empty();
}
public static final Object Killed = __init$Killed();
    private static Object __init$Killed() { return (java.util.function.Function<Object, Object>) (value0_i0) -> { return new __M$Effect_AVar.Killed(value0_i0); }; }
public static final Object Filled = __init$Filled();
    private static Object __init$Filled() { return (java.util.function.Function<Object, Object>) (value0_i0) -> { return new __M$Effect_AVar.Filled(value0_i0); }; }
public static final Object Empty = __init$Empty();
    private static Object __init$Empty() { return __M$Effect_AVar.__singleton$Empty.value; }
public static final Object $new = __init$$new();
    private static Object __init$$new() { return __M$Effect_AVar._newVar; }
public static final Object isKilled = __init$isKilled();
    private static Object __init$isKilled() { return (java.util.function.Function<Object, Object>) (v_0_i0) -> { return (((Object) (v_0_i0)) instanceof __M$Effect_AVar.Killed); }; }
public static final Object isFilled = __init$isFilled();
    private static Object __init$isFilled() { return (java.util.function.Function<Object, Object>) (v_0_i0) -> { return (((Object) (v_0_i0)) instanceof __M$Effect_AVar.Filled); }; }
public static final Object isEmpty = __init$isEmpty();
    private static Object __init$isEmpty() { return (java.util.function.Function<Object, Object>) (v_0_i0) -> { return (((Object) (v_0_i0)) instanceof __M$Effect_AVar.Empty); }; }
public static final Object ffiUtil = __init$ffiUtil();
    private static Object __init$ffiUtil() { return (new java.util.function.Supplier<Object>() { public Object get() { final Object __field0 = __M$Data_Either.Left; final Object __field1 = __M$Data_Either.Right; final Object __field2 = __M$Data_Maybe.__singleton$Nothing.value; final Object __field3 = __M$Data_Maybe.Just; final Object __field4 = __M$Effect_AVar.Killed; final Object __field5 = __M$Effect_AVar.Filled; final Object __field6 = __M$Effect_AVar.__singleton$Empty.value; return new __Record$65_6d_70_74_79_O$66_69_6c_6c_65_64_O$6a_75_73_74_O$6b_69_6c_6c_65_64_O$6c_65_66_74_O$6e_6f_74_68_69_6e_67_O$72_69_67_68_74_O(new String[]{"left", "right", "nothing", "just", "killed", "filled", "empty"}, __field6, __field5, __field3, __field4, __field0, __field2, __field1); } }).get(); }
public static final Object kill = __init$kill();
    private static Object __init$kill() { return (java.util.function.Function<Object, Object>) (err_0_i0) -> { return (java.util.function.Function<Object, Object>) (avar_1_i1) -> { return ((java.util.function.Function<Object, Object>) (((java.util.function.Function<Object, Object>) (((java.util.function.Function<Object, Object>) (__M$Effect_AVar._killVar)).apply(__M$Effect_AVar.ffiUtil))).apply(err_0_i0))).apply(avar_1_i1); }; }; }
public static final Object put = __init$put();
    private static Object __init$put() { return (java.util.function.Function<Object, Object>) (value_0_i0) -> { return (java.util.function.Function<Object, Object>) (avar_1_i1) -> { return (java.util.function.Function<Object, Object>) (cb_2_i2) -> { return ((java.util.function.Function<Object, Object>) (((java.util.function.Function<Object, Object>) (((java.util.function.Function<Object, Object>) (((java.util.function.Function<Object, Object>) (__M$Effect_AVar._putVar)).apply(__M$Effect_AVar.ffiUtil))).apply(value_0_i0))).apply(avar_1_i1))).apply(cb_2_i2); }; }; }; }
public static final Object read = __init$read();
    private static Object __init$read() { return (java.util.function.Function<Object, Object>) (avar_0_i0) -> { return (java.util.function.Function<Object, Object>) (cb_1_i1) -> { return ((java.util.function.Function<Object, Object>) (((java.util.function.Function<Object, Object>) (((java.util.function.Function<Object, Object>) (__M$Effect_AVar._readVar)).apply(__M$Effect_AVar.ffiUtil))).apply(avar_0_i0))).apply(cb_1_i1); }; }; }
public static final Object status = __init$status();
    private static Object __init$status() { return (java.util.function.Function<Object, Object>) (avar_0_i0) -> { return ((java.util.function.Function<Object, Object>) (((java.util.function.Function<Object, Object>) (__M$Effect_AVar._status)).apply(__M$Effect_AVar.ffiUtil))).apply(avar_0_i0); }; }
public static final Object take = __init$take();
    private static Object __init$take() { return (java.util.function.Function<Object, Object>) (avar_0_i0) -> { return (java.util.function.Function<Object, Object>) (cb_1_i1) -> { return ((java.util.function.Function<Object, Object>) (((java.util.function.Function<Object, Object>) (((java.util.function.Function<Object, Object>) (__M$Effect_AVar._takeVar)).apply(__M$Effect_AVar.ffiUtil))).apply(avar_0_i0))).apply(cb_1_i1); }; }; }
public static final Object tryPut = __init$tryPut();
    private static Object __init$tryPut() { return (java.util.function.Function<Object, Object>) (value_0_i0) -> { return (java.util.function.Function<Object, Object>) (avar_1_i1) -> { return ((java.util.function.Function<Object, Object>) (((java.util.function.Function<Object, Object>) (((java.util.function.Function<Object, Object>) (__M$Effect_AVar._tryPutVar)).apply(__M$Effect_AVar.ffiUtil))).apply(value_0_i0))).apply(avar_1_i1); }; }; }
public static final Object tryRead = __init$tryRead();
    private static Object __init$tryRead() { return (java.util.function.Function<Object, Object>) (avar_0_i0) -> { return ((java.util.function.Function<Object, Object>) (((java.util.function.Function<Object, Object>) (__M$Effect_AVar._tryReadVar)).apply(__M$Effect_AVar.ffiUtil))).apply(avar_0_i0); }; }
public static final Object tryTake = __init$tryTake();
    private static Object __init$tryTake() { return (java.util.function.Function<Object, Object>) (avar_0_i0) -> { return ((java.util.function.Function<Object, Object>) (((java.util.function.Function<Object, Object>) (__M$Effect_AVar._tryTakeVar)).apply(__M$Effect_AVar.ffiUtil))).apply(avar_0_i0); }; }
}
