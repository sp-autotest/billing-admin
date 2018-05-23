Ext.define('BSP.view.billing.BoWindowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.boWindowController',

    onReportBillingBtnInBoWindow: function() {
        var boWindow = this.view;
        var billingReportForm = boWindow.lookupReference('billingReportForm');
        var boFilesGrid = boWindow.lookupReference('boFilesGrid');
        var billingFiles = boWindow.billingFiles;
        var boFiles = boFilesGrid.getSelectionModel().getSelection();
        var tree = Ext.getCmp('billingTreeId');
        var store = tree.getStore();

        var billingFilesDto = new Array();
        var boFilesDto = new Array();
        Ext.each(billingFiles,function(billingFile){
            billingFilesDto.push({
                'id' : billingFile.data.id,
                'billingFileName' : billingFile.data.billingFileName
            });
        });
        Ext.each(boFiles,function(boFile){
            boFilesDto.push({
                'fileName' : boFile.data.fileName
            });
        });
        billingReportForm.getForm().setValues({
            'billingFiles' : Ext.encode(billingFilesDto),
            'boFiles' : Ext.encode(boFilesDto)
        });

        billingReportForm.submit({
            waitMsg: 'Формируем отчёт',
            timeout: '300000',
            success: function(fp,o) {
                boWindow.close();
                store.load();
                var billingReportResultWindow = Ext.create('BSP.view.billing.BillingReportResultWindow',{
                    billingReportResult: o.result
                });
                billingReportResultWindow.show();
            },
            failure: function(fp,o) {
                var response = Ext.util.JSON.decode(o.response.responseText);
                Ext.MessageBox.alert('Ошибка формирование отчёта',response.text);
            }
        });
    },

    onShowAllBoFilesInBoWindow: function(checkbox) {
        var boWindow = this.view;
        var boFilesGrid = boWindow.lookupReference('boFilesGrid');
        var store = boFilesGrid.getStore();
        if ( checkbox.checked ) {
            store.load({
                params: {'all': 'true'}
            })
        }
        else {
            store.load();
        }
    }

});
