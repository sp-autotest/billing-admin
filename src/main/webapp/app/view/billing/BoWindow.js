Ext.define('BSP.view.billing.BoWindow',{
    alias : 'widget.BoWindow',
    extend: 'Ext.window.Window',
    config: {
        billingFiles : null
    },
    requires: [
        'BSP.model.Bo',
        'BSP.store.BoStore',
        'BSP.view.billing.BoWindowController',
        'Ext.selection.CheckboxModel'
    ],
    controller: 'boWindowController',
    modal:true,
    title: 'БО файлы',
    height: 410,
    width: 500,
    reference: 'boWindow',
    layout: 'fit',
    closeable: 'true',
    dockedItems: [
        {
            xtype: 'form',
            url: 'mvc/billing/report',
            reference: 'billingReportForm',
            items: [
                {
                    xtype: 'gridpanel',
                    store: 'BoStore',
                    selType: 'checkboxmodel',
                    reference: 'boFilesGrid',
                    margin: '0 0 20 0',
                    layout:'fit',
                    viewConfig: {
                        stripeRows: true
                    },
                    height: 300,
                    columnLines: true,
                    columns: [
                        {
                            text: 'Имя файла',
                            dataIndex: 'fileName',
                            flex:1,
                            name: 'fileName'
                        },
                        {
                            xtype: 'datecolumn',
                            text: 'Дата создания',
                            dataIndex: 'createdDate'
                        },
                        {
                            text: 'Размер (б)',
                            dataIndex: 'size'
                        }
                    ],
                    listeners: {
                        render: function() {
                            this.store.load();
                        }
                    }
                },
                {xtype:'hidden', name:'billingFiles'},
                {xtype:'hidden', name:'boFiles'},
                {
                    xtype: 'checkboxfield',
                    flex: 1,
                    padding: 3,
                    fieldLabel: 'Показать все',
                    listeners: {
                        change: 'onShowAllBoFilesInBoWindow'
                    }
                },
                {
                    xtype: 'button',
                    flex: 1,
                    width: '100%',
                    padding: 3,
                    text: 'Начать обработку',
                    handler: 'onReportBillingBtnInBoWindow'
                }
            ]
        }
    ]

    /*
     {
     xtype: 'checkboxfield',
     width: 50,
     fieldLabel: 'Label',
     boxLabel: 'Box Label'
     },
     */

});
