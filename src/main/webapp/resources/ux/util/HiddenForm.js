Ext.ux.util.HiddenForm = function(url,fields){
    if (!Ext.isArray(fields))
        return;
    var body = Ext.getBody(),
        frame = body.createChild({
            tag:'iframe',
            cls:'x-hidden',
            id:'hiddenform-iframe',
            name:'iframe'
        }),
        form = body.createChild({
            tag:'form',
            cls:'x-hidden',
            id:'hiddenform-form',
            action: url,
            target:'iframe'
        });

    Ext.each(fields, function(el,i){
        if (!Ext.isArray(el))
            return false;
        form.createChild({
            tag:'input',
            type:'text',
            cls:'x-hidden',
            id: 'hiddenform-' + el[0],
            name: el[0],
            value: el[1]
        });
    });

    form.dom.submit();

    return frame;
}
