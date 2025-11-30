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
 * 回调接口，定义回调方法
 * @author WangBing
 */
interface ICallbackHolder {
    void callback();
}