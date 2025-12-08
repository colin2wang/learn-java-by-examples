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
 * 回调持有者，实现ICallbackHolder接口
 * @author WangBing
 */
@Slf4j
class CallbackHolder implements ICallbackHolder {
    @Override
    public void callback() {
        log.info(">>> callback()");
    }
}