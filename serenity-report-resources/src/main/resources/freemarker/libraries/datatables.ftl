<!-- DataTables -->
<link rel="stylesheet" type="text/css" href="datatables/1.11.3/datatables.css"/>
<script type="text/javascript" src="datatables/1.11.3/datatables.js"></script>
<script type="text/javascript" src="datatables/1.11.3/time-elapsed-dhms.js"></script>
<script type="text/javascript" src="datatables/1.11.3/time-elapsed-dhms.js"></script>
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
