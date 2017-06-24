package cn.appleye.eventtrigger.utils;


import java.lang.reflect.Constructor;

/**
 * Created by liuliaopu on 2017/6/24.
 * 反射工具类
 */

public class ReflectUtil {
    private static final String TAG = "ReflectUtil";

    /**
     * 根据类和参数获取实例
     * @param clz 类名
     * @param args 构造方法参数
     * */
    public static Object newInstance(Class<?> clz, Object[] args){
        try{
            Class objClz = Class.forName(clz.getName());

            //step 1 : 直接使用参数获取实例
            if(args == null || args.length == 0){//无参构造方法
                return clz.getConstructor().newInstance();
            }

            try{
                int argsLength = args.length;
                Class<?>[] paramCls = new Class[argsLength];
                for(int i=0; i<argsLength; i++){
                    paramCls[i] = args[i].getClass();
                }

                Constructor constructor = clz.getConstructor(paramCls);
                return constructor.newInstance(args);

            }catch (Exception e){
                //e.printStackTrace();
                LogUtil.d(TAG, "init instance failed by parameters directly! we will choose an appropriate constructor");
            }

            //step 2 : 通过参数类型来选择构造方法(因为存在重写问题，可能不会选对构造方法)
            Constructor[] constructors = objClz.getConstructors();//公有构造方法
            if(constructors == null || constructors.length == 0){
                throw new IllegalArgumentException("class : " + clz.getName() + " is not valid class?");
            }
            if(constructors.length == 1){//只有一个构造方法的情况下
                LogUtil.d(TAG, "we will create an object for the only one constructor");
                return constructors[0].newInstance(args);
            } else {
                //需要根据参数类型来选择构造方法
                int size = constructors.length;
                for(int i=0; i<size; i++){
                    Class<?>[] parameters = constructors[i].getParameterTypes();
                    if(parameters.length != args.length){
                        continue;
                    }

                    int paramSize = parameters.length;
                    boolean success = true;
                    for(int j=0; j<paramSize; j++){
                        //参数不对应
                        if(parameters[j]!=args[j].getClass() && !parameters[j].isAssignableFrom(args[j].getClass())){
                            success = false;
                            break;
                        }
                    }

                    if(success){
                        LogUtil.d(TAG, "we have chosen an appropriate constructor to create an object!");
                        return constructors[i].newInstance(args);
                    }
                }
            }
        }catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            throw new IllegalArgumentException("class : " + clz.getName() + " is not found");
        }catch (NoSuchMethodException nsme) {
            nsme.printStackTrace();
            throw new IllegalArgumentException("class : " + clz.getName() + " has no constructor like this");
        }catch (Exception e){
            e.printStackTrace();
            throw new IllegalArgumentException("OMD! Something unexpected errors happened");
        }

        return null;
    }
}
