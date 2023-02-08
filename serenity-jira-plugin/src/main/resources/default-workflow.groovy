when 'Open', {
    'success' should: 'Resolve Issue'
}

when 'Reopened', {
    'success' should: 'Resolve Issue'
}

when 'Resolved', {
    'failure' should: 'Reopen Issue'
}

when 'In Progress', {
    'success' should: ['Stop Progress','Resolve Issue']
}

when 'Closed', {
    'failure' should: 'Reopen Issue'
}
