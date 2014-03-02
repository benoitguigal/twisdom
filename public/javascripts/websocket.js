
var url = 'ws://' +  window.location.hostname + ':' + window.location.port + '/quotation';
var ws = new WebSocket(url);
ws.onmessage = function(event) {
    var data = JSON.parse(event.data)
    $('#quotation').text(data.text)
    $('#author').text(data.author)
    $('#user').text(data.user)
    $('#date').text(data.date)
}

