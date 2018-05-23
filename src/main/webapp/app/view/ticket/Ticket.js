Ext.define('BSP.view.ticket.Ticket',{
    extend: 'Ext.panel.Panel',
    alias: 'widget.app-ticket',

    requires: [
        'BSP.view.ticket.TicketController',
        'BSP.view.ticket.TicketModel',
        'BSP.view.ticket.TicketFormPanel'
    ],

    controller: 'ticket',
    viewModel: {
        type: 'ticket'
    },
    padding: 3,

    items: [
        {
            xtype: 'container',
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            items: [
                {
                    xtype: 'ticketFormPanel',
                    flex: 1,
                    width: 511,
                    bodyBorder: true,
                    bodyPadding: 10,
                    reference: 'ticketFindForm',
                    url : 'mvc/ticket/find',
                    title: 'Фильтр',
                    items: [
                        {
                            xtype: 'fieldset',
                            defaultType: 'datefield',
                            title: 'Диапазоны дат',
                            items: [
                                {
                                    anchor: '100%',
                                    fieldLabel: 'От',
                                    name: 'fromCreateDate',
                                    format: 'Y/m/d H:i:s',
                                    value: Ext.Date.add(new Date(),Ext.Date.DAY,-3),
                                    maxValue: new Date()
                                },
                                {
                                    xtype: 'datefield',
                                    anchor: '100%',
                                    fieldLabel: 'До',
                                    name: 'toCreateDate',
                                    format: 'Y/m/d H:i:s',
                                    value: new Date()
                                }
                            ]
                        },
                        {
                            xtype: 'fieldset',
                            defaultType: 'textfield',
                            title: 'Параметры заказа',
                            id: 'paramsOfOrder',
                            items: [
                                {
                                    fieldLabel: 'Номер билета',
                                    name: 'documentNumber',
                                    anchor: '100%',
                                    id: 'paramsOfOrder_documentNumber'
                                },
                                {
                                    fieldLabel: 'Approval code',
                                    name: 'approvalCode',
                                    anchor: '100%',
                                    id: 'paramsOfOrder_approvalCode'
                                }
                            ]
                        },
                        {
                            xtype: 'fieldset',
                            defaultType: 'textfield',
                            title: 'Параметры Карты',
                            id: 'paramsOfCard',
                            items: [
                                {
                                    fieldLabel: 'Номер карты',
                                    name: 'pan',
                                    id: 'paramsOfCard_pan',
                                    anchor: '100%',
                                    enforceMaxLength: true,
                                    maxLength: '23',
                                    maskRe: /[0-9.]/
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
                    xtype: 'container',
                    flex: 3,
                    layout: {
                        type: 'vbox',
                        align: 'stretch'
                    },
                    items: [
                        {
                            xtype: 'treepanel',
                            flex: 3,
                            height: 400,
                            width: 400,
                            bind: {
                                title: '{titleTreeGridDefault}'
                            },
                            rootVisible: false,
                            reference: 'ticketTree',
                            useArrows: true,
                            store: 'TicketStore',
                            viewConfig: {
                                getRowClass: function(record, index) {
                                    var fileType = record.data['fileType'];
                                    var fileName = record.data['fileName'];
                                    if ( 'POSTING' == fileType && fileName.toLowerCase().search('reject') != -1) {
                                        return 'reject-row';
                                    }
                                }
                            },
                            columns: [
                                {
                                    xtype: 'treecolumn',
                                    width: 100,
                                    dataIndex: 'id',
                                    text: 'ID',
                                    flex: 1
                                },
                                {

                                    dataIndex: 'transactionType',
                                    text: 'Тип транзакции'
                                },
                                {

                                    dataIndex: 'documentNumber',
                                    text: 'Номер билета'
                                },
                                {

                                    dataIndex: 'approvalCode',
                                    text: 'Approval code'
                                },
                                {

                                    dataIndex: 'pan',
                                    text: 'PAN'
                                },
                                {

                                    dataIndex: 'refNum',
                                    text: 'RefNum'
                                },
                                {

                                    dataIndex: 'amount',
                                    text: 'Сумма'
                                },
                                {

                                    dataIndex: 'currency',
                                    text: 'Валюта'
                                },
                                {

                                    dataIndex: 'createdAt',
                                    text: 'Дата создания'
                                },
                                {

                                    dataIndex: 'invoiceNumber',
                                    text: 'Invoice Number'
                                },
                                {

                                    dataIndex: 'fileType',
                                    text: 'Тип файла'
                                },
                                {
                                    dataIndex: 'fileName',
                                    text: 'Файл',
                                    renderer: function(value, metaData, record) {
                                        if ( null != value ) {
                                            return '<a title="Скачать ' + record.data['fileType'] + ' файл : ' + record.data['fileName'] + '" href="mvc/file/download?fileName=' + record.data['fileName'] + '&fileType=' + record.data['fileType'] + '">' + record.data['fileName'] + '</a>';
                                        }
                                    }
                                }
                            ]
                        }
                    ]
                }

            ]
        }
    ]
});
