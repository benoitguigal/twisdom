(function() {


   var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket
   var quotationSocket = new WS('ws://' +  window.location.hostname + ':9000/quotation')

    var receiveEvent = function(event) {
        var data = JSON.parse(event.data)
        $('#clock').text(data.text)
    }

})