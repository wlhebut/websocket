var wsObj =null;
var wsUri = null;
var userId= -1;
var lockReconnect = false;
var wsCreateHandler = null;

function createWebSocket() {
    var host = window.location.host;//带有端口号
    // userId = GetQueryString("userId");
    wsUri = "ws://" + host +"/websocket";
    try {
        wsObj = new WebSocket(wsUri);
        initWsEventHandle();
    } catch (e) {
        writeToScreen("执行关闭事件，开始重连");
        reconnect();
    }

}

function initWsEventHandle() {
    try {
        wsObj.onopen = function (evt) {
            onWsOpen(evt);
            // heartCheck.start();
        };
        wsObj.onmessage = function (evt) {
            onWsMessage(evt);
            // heartCheck.start();
        };
        wsObj.onclose = function (evt) {
            onWsClose(evt);
            reconnect()
        }
        wsObj.onerror = function (evt) {
            onWsError(evt);
            reconnect()
        }

    }catch (e) {
            writeToScreen("绑定事件没有成功！")
        reconnect();
    }


}

function onWsOpen(evt) {
    writeToScreen("CONNECTED");
}
function onWsClose(evt) {
    writeToScreen("DISCONNECTED");
}

function onWsError(evt) {
    writeToScreen(evt.data);
}
function onWsMessage(evt) {
    writeToScreen("CONNECTED");
}

function writeToScreen(message) {
    // if (DEBUG_FLAG) {
        $("#debuggerInfo").val($("#debuggerInfo").val() + "\n" +message);
    // }
}

function GetQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)","i");
    var r = window.location.search.substr(1).match(reg);
    var context = "";
    if(r!=null)
        context = r[2];
    reg = null;
    r = null;
    return context ==null || context =="" || context == "undefined" ? "" : context;
}

function reconnect() {
    if(lockReconnect){
        return;
    }

    writeToScreen("1秒后重连接！")

    lockReconnect = true;
    // 没有连接上会一直重连，设置延迟避免请求过多
    wsCreateHandler && clearTimeout(wsCreateHandler);
    wsCreateHandler = setTimeout(function () {
        writeToScreen("重连。。。" + wsUri);
        createWebSocket();
        lockReconnect = false;
        writeToScreen("重连完成！");

    },1000);

}


var heartCheck = {
    // 15s 内如果没有收到后台的消息，则认为连接断开了，需要重新连接
    timeout: 15000,
    timeoutObj: null,
    serverTimeoutObj: null,

    //重启
    reset: function () {
        clearTimeout(this.timeoutObj);
        clearTimeout(this.serverTimeoutObj);
        this.start();
    },

    //开启定时器
    start: function () {
        var self = this;
        this.timeoutObj && clearTimeout(this.timeoutObj);
        this.serverTimeoutObj && clearTimeout(this.serverTimeoutObj);

        this.timeoutObj = setTimeout(
            function () {
                writeToScreen("发送ping到后台");
                try {
                    wsObj.send("ping");
                }catch (e) {
                    writeToScreen("发送ping异常");
                }
                self.serverTimeoutObj = setTimeout(function () {
                    // 如果onclose会执行重连，reconnect,我们执行ws.close 就行了，如果直接执行reconnect 会触发onclose 导致重连两次
                    writeToScreen("没有收到后台数据，关闭连接");
                    wsObj.close();
                    reconnect();

                },self.timeout)
            }
        ,this.timeout);
    }
}