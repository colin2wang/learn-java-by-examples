package com.colin.java.callback;

/*
 * JavaScript Code:
 *
 *  var callback = function(){
 *    print(">>> callback()");
 *  };
 *  var func = function(cb){
 *     print(">>> func()");
 *     cb();
 *  };
 *  func(callback);
 */

/**
 * 回调使用者，实现ICallbackUser接口
 * @author WangBing
 */
class CallbackUser implements ICallbackUser {
    @Override
    public void func(ICallbackHolder callbackHolder) {
        System.out.println(">>> func()");
        callbackHolder.callback();
    }
}