public class __M$Effect_AVar {
    public static final Object FFI_STUB = new java.util.function.Function<Object, Object>() {
        public Object apply(Object arg) { throw new UnsupportedOperationException("Missing Java FFI in Effect.AVar"); }
    };
    public static Object _killVar = FFI_STUB;
    public static Object _killVar(Object... args) { throw new UnsupportedOperationException("Missing Java FFI: Effect.AVar._killVar"); }
    public static Object _newVar = FFI_STUB;
    public static Object _newVar(Object... args) { throw new UnsupportedOperationException("Missing Java FFI: Effect.AVar._newVar"); }
    public static Object _putVar = FFI_STUB;
    public static Object _putVar(Object... args) { throw new UnsupportedOperationException("Missing Java FFI: Effect.AVar._putVar"); }
    public static Object _readVar = FFI_STUB;
    public static Object _readVar(Object... args) { throw new UnsupportedOperationException("Missing Java FFI: Effect.AVar._readVar"); }
    public static Object _status = FFI_STUB;
    public static Object _status(Object... args) { throw new UnsupportedOperationException("Missing Java FFI: Effect.AVar._status"); }
    public static Object _takeVar = FFI_STUB;
    public static Object _takeVar(Object... args) { throw new UnsupportedOperationException("Missing Java FFI: Effect.AVar._takeVar"); }
    public static Object _tryPutVar = FFI_STUB;
    public static Object _tryPutVar(Object... args) { throw new UnsupportedOperationException("Missing Java FFI: Effect.AVar._tryPutVar"); }
    public static Object _tryReadVar = FFI_STUB;
    public static Object _tryReadVar(Object... args) { throw new UnsupportedOperationException("Missing Java FFI: Effect.AVar._tryReadVar"); }
    public static Object _tryTakeVar = FFI_STUB;
    public static Object _tryTakeVar(Object... args) { throw new UnsupportedOperationException("Missing Java FFI: Effect.AVar._tryTakeVar"); }
    public static Object empty = FFI_STUB;
    public static Object empty(Object... args) { throw new UnsupportedOperationException("Missing Java FFI: Effect.AVar.empty"); }

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
public static final Object Killed = (java.util.function.Function<Object, Object>) (value0_i0) -> { return new __M$Effect_AVar.Killed(value0_i0); };
public static final Object Filled = (java.util.function.Function<Object, Object>) (value0_i0) -> { return new __M$Effect_AVar.Filled(value0_i0); };
public static final Object Empty = __M$Effect_AVar.__singleton$Empty.value;
public static final Object $new = __M$Effect_AVar._newVar;
public static final Object isKilled = (java.util.function.Function<Object, Object>) (v_0_i0) -> { return (((Object) (v_0_i0)) instanceof __M$Effect_AVar.Killed); };
public static final Object isFilled = (java.util.function.Function<Object, Object>) (v_0_i0) -> { return (((Object) (v_0_i0)) instanceof __M$Effect_AVar.Filled); };
public static final Object isEmpty = (java.util.function.Function<Object, Object>) (v_0_i0) -> { return (((Object) (v_0_i0)) instanceof __M$Effect_AVar.Empty); };
public static final Object ffiUtil = (new java.util.function.Supplier<Object>() { public Object get() { final Object __field0 = __M$Data_Either.Left; final Object __field1 = __M$Data_Either.Right; final Object __field2 = __M$Data_Maybe.__singleton$Nothing.value; final Object __field3 = __M$Data_Maybe.Just; final Object __field4 = __M$Effect_AVar.Killed; final Object __field5 = __M$Effect_AVar.Filled; final Object __field6 = __M$Effect_AVar.__singleton$Empty.value; return new __Record$65_6d_70_74_79_O$66_69_6c_6c_65_64_O$6a_75_73_74_O$6b_69_6c_6c_65_64_O$6c_65_66_74_O$6e_6f_74_68_69_6e_67_O$72_69_67_68_74_O(new String[]{"left", "right", "nothing", "just", "killed", "filled", "empty"}, __field6, __field5, __field3, __field4, __field0, __field2, __field1); } }).get();
public static final Object kill = (java.util.function.Function<Object, Object>) (err_0_i0) -> { return (java.util.function.Function<Object, Object>) (avar_1_i1) -> { return ((java.util.function.Function<Object, Object>) (((java.util.function.Function<Object, Object>) (((java.util.function.Function<Object, Object>) (__M$Effect_AVar._killVar)).apply(__M$Effect_AVar.ffiUtil))).apply(err_0_i0))).apply(avar_1_i1); }; };
public static final Object put = (java.util.function.Function<Object, Object>) (value_0_i0) -> { return (java.util.function.Function<Object, Object>) (avar_1_i1) -> { return (java.util.function.Function<Object, Object>) (cb_2_i2) -> { return ((java.util.function.Function<Object, Object>) (((java.util.function.Function<Object, Object>) (((java.util.function.Function<Object, Object>) (((java.util.function.Function<Object, Object>) (__M$Effect_AVar._putVar)).apply(__M$Effect_AVar.ffiUtil))).apply(value_0_i0))).apply(avar_1_i1))).apply(cb_2_i2); }; }; };
public static final Object read = (java.util.function.Function<Object, Object>) (avar_0_i0) -> { return (java.util.function.Function<Object, Object>) (cb_1_i1) -> { return ((java.util.function.Function<Object, Object>) (((java.util.function.Function<Object, Object>) (((java.util.function.Function<Object, Object>) (__M$Effect_AVar._readVar)).apply(__M$Effect_AVar.ffiUtil))).apply(avar_0_i0))).apply(cb_1_i1); }; };
public static final Object status = (java.util.function.Function<Object, Object>) (avar_0_i0) -> { return ((java.util.function.Function<Object, Object>) (((java.util.function.Function<Object, Object>) (__M$Effect_AVar._status)).apply(__M$Effect_AVar.ffiUtil))).apply(avar_0_i0); };
public static final Object take = (java.util.function.Function<Object, Object>) (avar_0_i0) -> { return (java.util.function.Function<Object, Object>) (cb_1_i1) -> { return ((java.util.function.Function<Object, Object>) (((java.util.function.Function<Object, Object>) (((java.util.function.Function<Object, Object>) (__M$Effect_AVar._takeVar)).apply(__M$Effect_AVar.ffiUtil))).apply(avar_0_i0))).apply(cb_1_i1); }; };
public static final Object tryPut = (java.util.function.Function<Object, Object>) (value_0_i0) -> { return (java.util.function.Function<Object, Object>) (avar_1_i1) -> { return ((java.util.function.Function<Object, Object>) (((java.util.function.Function<Object, Object>) (((java.util.function.Function<Object, Object>) (__M$Effect_AVar._tryPutVar)).apply(__M$Effect_AVar.ffiUtil))).apply(value_0_i0))).apply(avar_1_i1); }; };
public static final Object tryRead = (java.util.function.Function<Object, Object>) (avar_0_i0) -> { return ((java.util.function.Function<Object, Object>) (((java.util.function.Function<Object, Object>) (__M$Effect_AVar._tryReadVar)).apply(__M$Effect_AVar.ffiUtil))).apply(avar_0_i0); };
public static final Object tryTake = (java.util.function.Function<Object, Object>) (avar_0_i0) -> { return ((java.util.function.Function<Object, Object>) (((java.util.function.Function<Object, Object>) (__M$Effect_AVar._tryTakeVar)).apply(__M$Effect_AVar.ffiUtil))).apply(avar_0_i0); };
}
