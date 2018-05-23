Ext.define('BSP.view.billing.BillingReportResultWindow',{
    alias : 'widget.BillingReportResultWindow',
    extend: 'Ext.window.Window',
    config: {
        billingReportResult : null
    },
    constructor: function(cfg) {
        this.billingReportResult = cfg.billingReportResult;

        var panels = new Array();
        Ext.each(this.billingReportResult.boFiles,function(boFile){
            var boPanel = Ext.create('Ext.grid.Panel',{
                title: 'БО файл: ' + boFile.name,
                store: {
                    fields: ['title',{name:'value',type:'int'}],
                    proxy: {
                        type: 'memory',
                        reader: {
                            type: 'json'
                        }
                    },
                    data: [
                        {'title': 'Кол-во записей','value':boFile.totalRecords},
                        {'title': 'Кол-во успешных записей','value':boFile.successRecords},
                        {'title': 'Кол-во ошибочных записей','value':boFile.errorRecords},
                        {'title': 'Кол-во фрод записей','value':boFile.fraudRecords},
                        {'title': 'Кол-во депозитов','value':boFile.depositRecords},
                        {'title': 'Кол-во рефандов','value':boFile.refundRecords}
                    ]
                },
                columns: [
                    {text: 'Результат',dataIndex:'title',flex:2},
                    {text: 'Количество',dataIndex: 'value',flex:1}
                ]
            });
            panels.push(boPanel);
        });

        Ext.each(this.billingReportResult.billingFiles,function(billingFile){
            var billingPanel = Ext.create('Ext.grid.Panel',{
                title: 'Биллинговые файлы',
                store: {
                    fields: ['title',{name:'value'}],
                    proxy: {
                        type: 'memory',
                        reader: {
                            type: 'json'
                        }
                    },
                    data: [
                        {'title':'Имя','value': billingFile.name}
                    ]
                },
                columns: [
                    {text: 'Результат',dataIndex:'title',flex:1},
                    {text: 'Количество',dataIndex: 'value',flex:2}
                ]
            });
            panels.push(billingPanel);
        });

        var reportPanel = Ext.create('Ext.grid.Panel',{
            title: 'Данные по отчёту',
            store: {
                fields: ['title',{name:'value',type:'int'}],
                proxy: {
                    type: 'memory',
                    reader: {
                        type: 'json'
                    }
                },
                data: [
                    {'title': 'Кол-во успешных депозитов','value': this.billingReportResult.reportData.successDepositRecordsCount},
                    {'title': 'Кол-во успешных возвратов','value': this.billingReportResult.reportData.successCreditRecordsCount},
                    {'title': 'Кол-во реджект депозитов','value': this.billingReportResult.reportData.rejectDepositRecordsCount},
                    {'title': 'Кол-во реджект возвратов','value': this.billingReportResult.reportData.rejectCreditRecordsCount}
                ]
            },
            columns: [
                {text: 'Результат',dataIndex:'title',flex:2},
                {text: 'Количество',dataIndex: 'value',flex:1}
            ]
        });
        panels.push(reportPanel);
        /*
        Ext.each(this.billingReportResult.reportFiles,function(reportFile){
            var reportPanel = Ext.create('Ext.grid.Panel',{
                title: 'Файл отчёта: ' + reportFile.name,
                store: {
                    fields: ['title',{name:'value',type:'int'}],
                    proxy: {
                        type: 'memory',
                        reader: {
                            type: 'json'
                        }
                    },
                    data: [
                        {'title': 'Кол-во успешных депозитов','value': reportFile.successDepositRecordsCount},
                        {'title': 'Кол-во успешных возвратов','value': reportFile.successCreditRecordsCount},
                        {'title': 'Кол-во реджект депозитов','value': reportFile.rejectDepositRecordsCount},
                        {'title': 'Кол-во реджект возвратов','value': reportFile.rejectCreditRecordsCount},
                    ]
                },
                columns: [
                    {text: 'Результат',dataIndex:'title',flex:2},
                    {text: 'Количество',dataIndex: 'value',flex:1}
                ]
            });
            panels.push(reportPanel);
        });
        */

        this.items = [
            {
                xtype: 'container',
                autoScroll: true,
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                items : panels
            }
        ];

        this.callParent(arguments);
    },
    modal: true,
    title: 'Результат формирования отчётов',
    height: 410,
    width: 600,
    reference: 'billingReportResultWindow',
    layout: 'fit',
    closeable: 'true',
    autoScroll: true
});
