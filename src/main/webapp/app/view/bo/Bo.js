Ext.define('BSP.view.bo.Bo',{
    extend: 'Ext.panel.Panel',
    alias: 'widget.app-bo',

    requires: [
        'BSP.view.bo.BoController',
        'BSP.view.bo.BoModel'
    ],

    controller: 'bo',
    viewModel: {
        type: 'bo'
    },
    padding: 3,

    items: [
        {
            xtype: 'form',
            title: 'Загрузка файла',
            reference: 'boUploadForm',
            url: 'mvc/bo/upload',
            items: [
                {
                    xtype: 'container',
                    layout: {
                        type: 'hbox',
                        align: 'stretch'
                    },
                    items: [
                        {
                            xtype: 'filefield',
                            fieldLabel: 'Файл',
                            labelWidth: 35,
                            msgTarget: 'side',
                            allowBlank: false,
                            anchor: '100%',
                            buttonText: 'Выбрать файл...',
                            name: 'file',
                            reference: 'boUploadFile',
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
                            handler : 'onUploadBoFile'
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
                    bodyBorder: true,
                    bodyPadding: 10,
                    reference: 'boFindForm',
                    url : 'mvc/bo/find',
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
                                    margin: '0 10 0 0',
                                    handler: 'onFindButton'
                                },
                                {
                                    xtype: 'button',
                                    flex: 1,
                                    text: 'Сбросить',
                                    handler: 'onResetButton'
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'container',
                    flex: 3,
                    layout: {
                        type: 'vbox',
                        align: 'stretch'
                    },
                    items: [
                        {
                            xtype: 'gridpanel',
                            flex: 1,
                            title: 'Загруженные файлы (на файловую систему)',
                            reference: 'boGrid',
                            store: 'BoStore',
                            layout:'fit',
                            viewConfig: {
                                stripeRows: true
                            },
                            height: 300,
                            columnLines: true,
                            listeners: {
                                itemclick: 'onItemClick',
                                selectionchange: 'onSelectedClick'
                            },
                            columns: [
                                {
                                    xtype: 'gridcolumn',
                                    flex: 2,
                                    dataIndex: 'fileName',
                                    text: 'Имя файла'
                                },
                                {
                                    xtype: 'datecolumn',
                                    flex: 1,
                                    dataIndex: 'createdDate',
                                    format: 'Y/m/d H:i:s',
                                    text: 'Дата загрузки'
                                },
                                {
                                    flex: 1,
                                    dataIndex: 'size',
                                    text: 'Размер (б)'
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
                                            reference: 'downloadBoForm',
                                            standardSubmit: true,
                                            url: 'mvc/file/download',
                                            items: [
                                                {xtype:'hidden', name:'fileName'},
                                                {xtype:'hidden', name:'fileType',value:'BO'},
                                                {
                                                    xtype: 'button',
                                                    text: 'Скачать',
                                                    id: 'download-bo-btn',
                                                    disabled: true,
                                                    handler: 'onDownloadBoBtn'
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
        }
    ]
});
