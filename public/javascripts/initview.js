$.get(
    '/lastQuotation',
    {},
    function(data) {
        if(data != null) {
            var d = JSON.parse(data);
            $('#quotation').text(d.text)
            $('#author').text(d.author)
            $('#user').text(d.user)
            $('#date').text(d.date)
        }
    }
)

