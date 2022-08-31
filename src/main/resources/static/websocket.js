'use strict';

import { Snowflake } from './snowflake.js'

class Websocket {
    constructor(configs) {
        this.configs = {
          debug: "调试"
        };
        if (configs) {
            Object.assign(this.configs, configs);
        }
    };

    init() {
        // 检测 DOMContentLoaded 是否已完成
        document.readyState !== 'loading' ? this._doStart(this.configs) : document.addEventListener('DOMContentLoaded', () => this._doStart(this.configs));
    }

    _doStart(configs) {
        const socket = new SockJS("$/api/kyyee/v2/sps/websocket/endpoint-wisely", null, { transports: ['websocket', 'xhr-polling', 'eventsource'], timeout: 30000 })
        const clientId = Snowflake.getId();
        let stompClient = Stomp.over(socket)
        // headers认证信息
        let headers = {
          "id": clientId,
          "content-type": "application/json",
          "user": encodeURIComponent("usercode:admin&username:admin")
        };
        stompClient.connect(headers, () => {  // 连接成功回调函数
          console.log('connected')
          this.stompClient.subscribe("/user/" + clientId + "notification-channel", (res) => {
            console.log(res.body);
            const websocketValue = document.getElementById('websocketValue');
            websocketValue.textContent=JSON.stringify(res.body);
          }, headers)
        }, (e) => { // 连接失败时再次调用函数
          console.log('5秒后重连' + e)
          stompClient.disconnect();
          setTimeout(() => {
            this._doStart(configs)
          }, 5000);
        })
        if (configs.debug) {
          stompClient.debug = configs.debug
        }
    }
}

new Websocket({debug: true}).init();
