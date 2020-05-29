<!-- DataTables -->
<link type="text/css" href="datatables/1.10.20/datatables.min.css" rel="stylesheet" />
<script type="text/javascript" src="datatables/1.10.20/datatables.min.js"></script>
<script type="text/javascript" src="datatables/1.10.20/time-elapsed-dhms.js"></script>

<link href="bootstrap/css/bootstrap.min.css" rel="stylesheet">
<script src="bootstrap/js/bootstrap.min.js"></script>

<script type="text/javascript" src="datatables/1.10.20/datatables.bootstrap4.min.js"></script>
<link type="text/css" href="datatables/1.10.20/datatables.bootstrap4.min.css" rel="stylesheet" />

<script>
    jQuery.fn.dataTable.ext.type.order['time-elapsed-dhms-pre'] = function(data) {

        var matches = data.match(/^(\d+(?:\.\d+)?)\s*([a-z]+)/i);
        var multipliers = {
            ms: 1,
            s: 1000,
            m: 60000,
            h: 3600000,
            d: 86400000
        };

        if (matches) {
            var multiplier = multipliers[matches[2].toLowerCase()];
            return parseFloat(matches[1]) * multiplier;
        } else {
            return -1;
        };
    };
</script>
