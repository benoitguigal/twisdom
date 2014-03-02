
var url = 'ws://' +  window.location.hostname + ':9000/quotation';
var ws = new WebSocket(url);
ws.onmessage = function(event) {
    var data = JSON.parse(event.data)
    $('#quotation').text(data.text)
    $('#author').text(data.author)
    $('#user').text(data.user)
    $('#date').text(data.date)
}

