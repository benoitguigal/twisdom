// init with a simple http call before opening web socket
$.get(
    '/lastQuotation',
    {},
    function(data) {
        updateView(data)
    }
)

// open a websocket for streaming
var url = 'ws://' +  window.location.hostname + ':' + window.location.port + '/quotation';
var ws = new WebSocket(url);
ws.onmessage = function(event) {
    var data = JSON.parse(event.data)
    updateView(data)
}

function updateView(data) {
    if(data != null) {
        $('#quotation').text("\"" + data.text + "\"");
        $('#author').text(data.author);
        $('#profile').css("background", "url(" + data.status.user.imageUrl + ")");
        $('#name').text(data.status.user.name);
        $('#screenName').text("@" + data.status.user.screenName);
        $('#original-tweet').text("View the original tweet");
        $('#original-tweet').attr("href", "https://twitter.com/intent/user?user_id=" + data.status.user.id);
    }
}