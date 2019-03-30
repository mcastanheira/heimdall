const AUTOREFRESH_INTERVAL = 60000;

function setAutoRefresh() {
    setInterval(() => document.location.href = document.location.pathname, AUTOREFRESH_INTERVAL);
}

function startServiceFormValidation() {
    
    $('#service-form').submit((event) => {
        
        const errors = [];

        if ($('input[name="name"]').val() == '') {
            errors.push('Service name cannot be blank!');
        }

        if ($('input[name="host"]').val() == '') {
            errors.push('Service host cannot be blank!');
        }

        if ($('input[name="port"]').val() == '') {
            errors.push('Service port cannot be blank!');
        }
        
        if ($('input[name="heartbeat"]').val() == '') {
            errors.push('Service heartbeat cannot be blank!');
        }

        if (errors.length > 0) {
            alert(errors.join('\n\n'));
            event.preventDefault();
        }
    });
    
}