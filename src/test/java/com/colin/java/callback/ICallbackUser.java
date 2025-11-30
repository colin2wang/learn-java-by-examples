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
 * 使用回调的接口，定义调用回调方法的功能
 * @author WangBing
 */
interface ICallbackUser {
    void func(ICallbackHolder callbackHolder);
}