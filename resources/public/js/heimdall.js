const AUTOREFRESH_INTERVAL = 60000;

function setAutoRefresh() {
    setInterval(() => document.location.href = document.location.pathname, AUTOREFRESH_INTERVAL);
}

function startServiceFormValidation() {
    
    const originFn = (value) => {

        if (value == 'local') {

            $('input[name="host"]').val('');
            $('input[name="host"]').prop('disabled', true);

            $('input[name="restart"]').prop('disabled', false);

            $('input[name="command"]').prop('disabled', false);
        } else {

            $('input[name="host"]').prop('disabled', false);

            $('input[name="restart"]').prop('checked', false);
            $('input[name="restart"]').prop('disabled', true);

            $('input[name="command"]').val('');
            $('input[name="command"]').prop('disabled', true);
        }
    };

    originFn($('input[name="origin"]:checked').val());

    $('input[name="origin"]').change((event) => {
        originFn(event.target.value);
    });

    $('#service-form').submit((event) => {
        
        const errors = [];

        if ($('input[name="name"]').val() == '') {
            errors.push('Service name cannot be blank!');
        }

        if ($('input[name="origin"]:checked').val() == undefined) {
            errors.push('Service origin cannot be blank!');
        }

        if (($('input[name="origin"]:checked').val() == 'remote') 
            && ($('input[name="host"]').val() == '')) {
            
            errors.push('Remote service host cannot be blank!');
        }

        if ($('input[name="port"]').val() == '') {
            errors.push('Service port cannot be blank!');
        }
        
        if ($('input[name="heartbeat"]').val() == '') {
            errors.push('Service heartbeat cannot be blank!');
        }

        if (($('input[name="restart"]').prop('checked')) 
            && ($('input[name="command"]').val() == '')) {
            
            errors.push('Inform service restart command!');
        }

        if (errors.length > 0) {
            alert(errors.join('\n\n'));
            event.preventDefault();
        }
    });
    
}