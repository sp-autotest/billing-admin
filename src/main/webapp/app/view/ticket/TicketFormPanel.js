Ext.define('BSP.view.ticket.TicketFormPanel',{
    extend: 'Ext.form.Panel',
    xtype: 'ticketFormPanel',


    isValid: function() {
        var documentNumberField = Ext.getCmp('paramsOfOrder_documentNumber');
        var approvalCodeField = Ext.getCmp('paramsOfOrder_approvalCode');
        var panField = Ext.getCmp('paramsOfCard_pan');

        if ( null != documentNumberField && documentNumberField.isValid() ) {
            if ( !Ext.isEmpty(documentNumberField.value) ) return true;
        }
        if ( null != approvalCodeField && approvalCodeField.isValid() ) {
            if ( !Ext.isEmpty(approvalCodeField.value) ) return true;
        }
        if ( null != panField && panField.isValid()) {
            if ( !Ext.isEmpty(panField.value) ) return true;
        }

        documentNumberField.markInvalid('Одно из полей должной быть заполнено');
        approvalCodeField.markInvalid('Одно из полей должной быть заполнено');
        panField.markInvalid('Одно из полей должной быть заполнено');
        return false;

    }
});
