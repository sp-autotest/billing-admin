Ext.define('BSP.view.billing.Billing',{
    extend: 'Ext.panel.Panel',
    alias: 'widget.app-billing',

    requires: [
        'BSP.view.billing.BillingController',
        'BSP.view.billing.BillingModel',
        'BSP.model.Bo',
        'BSP.view.billing.BoWindow'
    ],

    controller: 'billing',
    viewModel: {
        type: 'billing'
    },
    padding: 3,

    items: [
        {
            xtype: 'form',
            title: 'Загрузка файла',
            reference: 'billingUploadForm',
            url: 'mvc/billing/upload',
            items: [
                {
                    xtype: 'container',
                    layout: {
                        type: 'hbox',
                        align: 'stretch'
                    },
                    items: [
                        {
                            xtype: 'datefield',
                            fieldLabel: 'Отчётная дата:',
                            name: 'businessDate',
                            value: new Date(),
                            margin: '0 20 0 3'
                        },
                        {
                            xtype: 'filefield',
                            fieldLabel: 'Файл',
                            labelWidth: 35,
                            msgTarget: 'side',
                            allowBlank: false,
                            anchor: '100%',
                            buttonText: 'Выбрать файл...',
                            name: 'file',
                            reference: 'billingUploadFile',
                            margin: '0 10 0 0',
                            size: 50,
                            listeners: {
                                change: function(fld, value) {
                                    var newValue = value.replace(/C:\\fakepath\\/g, '');
                                    fld.setRawValue(newValue);
                                }
                            }
                        },
                        {
                            xtype: 'button',
                            text: 'Загрузить',
                            handler : 'onUploadBillingFile'
                        }
                    ]
                }
            ]
        },
        {
            xtype: 'container',
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            items: [
                {
                    xtype: 'form',
                    flex: 1,
                    width: 511,
//                    collapseDirection: 'left',
//                    collapsible: true,
                    bodyBorder: true,
                    bodyPadding: 10,
                    reference: 'billingFindForm',
                    url : 'mvc/billing/find',
                    title: 'Фильтр',
                    items: [
                        {
                            xtype: 'fieldset',
                            defaultType: 'datefield',
                            title: 'Диапазоны дат',
                            items: [
                                {
                                    xtype: 'datefield',
                                    anchor: '100%',
                                    fieldLabel: 'От',
                                    name: 'fromCreateDate',
                                    format: 'Y/m/d H:i:s',
                                    value: Ext.Date.add(new Date(),Ext.Date.DAY,-7),
                                    maxValue: new Date()
                                },
                                {
                                    xtype: 'datefield',
                                    anchor: '100%',
                                    fieldLabel: 'До',
                                    name: 'toCreateDate',
                                    format: 'Y/m/d H:i:s',
                                    value: Ext.Date.add(new Date(),Ext.Date.HOUR,1)
                                }
                            ]
                        },
                        {
                            xtype: 'fieldset',
                            defaultType: 'textfield',
                            title: 'Параметры файла',
                            items: [
                                {
                                    xtype: 'radiogroup',
                                    column: 1,
                                    vertical: true,
                                    items: [
                                        {
                                            name: 'mode',
                                            boxLabel: 'По биллинговому файлу',
                                            checked: true,
                                            inputValue: 'BY_BILLING'
                                        },
                                        {
                                            name: 'mode',
                                            boxLabel: 'По связанным файлам',
                                            inputValue: 'BY_LINKED'
                                        }
                                    ]
                                },
                                {
                                    xtype: 'textfield',
                                    anchor: '100%',
                                    fieldLabel: 'Имя файла',
                                    name: 'filename'
                                }
                            ]
                        },
                        {
                            xtype: 'fieldset',
                            defaultType: 'textfield',
                            title: 'Другое',
                            items: [
                                {
                                    xtype: 'combobox',
                                    fieldLabel: 'Авиакомпания',
                                    emptyText: 'Все',
                                    store: 'CarrierStore',
                                    displayField: 'name',
                                    valueField: 'id',
                                    name: 'carrierId'
                                }
                            ]
                        },
                        {
                            xtype: 'container',
                            layout: {
                                type: 'hbox',
                                align: 'stretch',
                                pack: 'center'
                            },
                            items: [
                                {
                                    xtype: 'button',
                                    flex: 1,
                                    text: 'Найти',
                                    handler: 'onFindButton',
                                    margin: '0 10 0 0'
                                },
                                {
                                    xtype: 'button',
                                    type: 'reset',
                                    flex: 1,
                                    text: 'Сброс',
                                    handler: 'onResetButton'
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'treepanel',
                    flex: 3,
                    height: 400,
                    //width: 400,
                    bind: {
                        title : '{titleTreeGridDefault}'
                    },
                    store: 'BillingStore',
                    reference: 'billingTree',
                    id : 'billingTreeId',
                    rootVisible: false,
                    useArrows: true,
                    multiSelect: true,

                    listeners: {
                        itemclick: 'onTreeItemClick',
                        selectionchange: 'onTreeSelectedClick'
                    },
                    columns: [
                        {
                            xtype: 'treecolumn',
                            dataIndex: 'id',
                            text: 'ID',
                            flex: 1
                        },
                        {
                            dataIndex: 'name',
                            text: 'Имя файла',
                            flex: 4
                        },
                        {
                            dataIndex: 'fileType',
                            text: 'Тип файла',
                            flex: 2
                        },
                        {
                            dataIndex: 'businessDate',
                            text: 'Отчётная дата',
                            renderer: Ext.util.Format.dateRenderer('Y/m/d'),
                            flex: 1
                        },
                        {
                            dataIndex: 'createdDate',
                            text: 'Дата создания',
                            renderer: Ext.util.Format.dateRenderer('Y/m/d H:i:s'),
                            flex: 2
                        },
                        {
                            dataIndex: 'format',
                            text: 'Формат',
                            flex: 1
                        },
                        {
                            dataIndex: 'iataCode',
                            text: 'IATA код',
                            flex: 1
                        }
                    ],
                    dockedItems: [
                        {
                            xtype: 'toolbar',
                            dock: 'bottom',
                            items: [
                                {
                                    xtype: 'form',
                                    reference: 'downloadBillingForm',
                                    standardSubmit: true,
                                    url: 'mvc/file/download',
                                    items: [
                                        {xtype:'hidden', name:'node'},
                                        {xtype:'hidden', name:'fileType'},
                                        {xtype:'hidden', name:'fileName'},
                                        {
                                            xtype: 'button',
                                            text: 'Скачать',
                                            id: 'download-billing-btn',
                                            disabled: true,
                                            handler: 'onDownloadBillingBtn'
                                        }
                                    ]
                                },
                                {
                                    xtype: 'form',
                                    reference: 'convertBillingForm',
                                    url: 'mvc/billing/convert',
                                    items: [
                                        {xtype:'hidden', name:'billingFiles'},
                                        {
                                            xtype: 'button',
                                            id: 'convert-billing-btn',
                                            text: 'Конвертировать',
                                            disabled: true,
                                            handler: 'onConvertBillingBtn'
                                        }
                                    ]
                                },
                                {
                                    xtype: 'form',
                                    reference: 'revertBillingForm',
                                    url: 'mvc/billing/revert_posting_file',
                                    items: [
                                        {xtype:'hidden', name:'node'},
                                        {
                                            xtype: 'button',
                                            id: 'revert-billing-btn',
                                            text: 'Аннулировать',
                                            disabled: true,
                                            handler: 'onRevertBillingBtn'
                                        }
                                    ]
                                },
                                {
                                    xtype: 'form',
                                    reference: 'reportBillingForm',
                                    items : [
                                        {
                                            xtype: 'button',
                                            id: 'report-billing-btn',
                                            text: 'Сформировать отчёт',
                                            disabled: true,
                                            handler: 'onReportBillingBtnShowBoWindow'
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ]

});
