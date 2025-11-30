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
 * 回调持有者，实现ICallbackHolder接口
 * @author WangBing
 */
class CallbackHolder implements ICallbackHolder {
    @Override
    public void callback() {
        System.out.println(">>> callback()");
    }
}