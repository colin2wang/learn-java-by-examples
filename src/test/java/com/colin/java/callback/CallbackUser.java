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

import lombok.extern.slf4j.Slf4j;

/**
 * 回调使用者，实现ICallbackUser接口
 * @author WangBing
 */
@Slf4j
class CallbackUser implements ICallbackUser {
    @Override
    public void func(ICallbackHolder callbackHolder) {
        log.info(">>> func()");
        callbackHolder.callback();
    }
}