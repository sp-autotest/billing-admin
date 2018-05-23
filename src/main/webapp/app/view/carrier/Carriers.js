Ext.define('BSP.view.carrier.Carriers', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.app-carriers',
    requires: [
        'BSP.controller.CarriersController'
    ],
    controller: 'carriers',
    padding: 3,
    items: [
        {
            xtype: 'tabpanel',
            items: [
                {
                    title: 'Авиакомпании',
                    items: [
                        {
                            xtype: 'gridpanel',
                            height: 500,
                            flex: 1,
                            store: 'CarrierStore',
                            viewConfig: {
                                stripeRows: true
                            },
                            tools: [{
                                type: 'plus',
                                tooltip: 'Добавить авиакомпанию',
                                handler: 'onAddCarrierButtonPressed'
                            }
                            ],
                            columnLines: true,
                            listeners: {
                                itemclick: 'onCarrierClick'
                            },
                            columns: [
                                {
                                    xtype: 'gridcolumn',
                                    flex: 1,
                                    dataIndex: 'id',
                                    text: 'id'
                                }, {
                                    xtype: 'gridcolumn',
                                    flex: 1,
                                    dataIndex: 'name',
                                    text: 'Название'
                                },
                                {
                                    xtype: 'gridcolumn',
                                    flex: 1,
                                    dataIndex: 'iataCode',
                                    text: 'IATA код'
                                },
                                {
                                    xtype: 'gridcolumn',
                                    flex: 1,
                                    dataIndex: 'mcc',
                                    text: 'MCC'
                                },
                                {
                                    xtype: 'gridcolumn',
                                    flex: 1,
                                    dataIndex: 'createdAt',
                                    formatter: 'date("d-m-Y G:i:s")',
                                    text: 'Дата добавления'
                                },
                                {
                                    xtype: 'gridcolumn',
                                    flex: 1,
                                    dataIndex: 'billingSystems',
                                    text: 'Биллинговые системы'
                                }
                            ]
                        }
                    ]
                },
                {
                    padding: 3,
                    title: 'Терминалы',
                    layout: 'hbox',
                    align: 'stretch',
                    items: [
                        {
                            minWidth: 310,
                            bodyBorder: true,
                            items: [
                                {
                                    xtype: 'fieldset',
                                    defaultType: 'datefield',
                                    title: 'Фильтр',
                                    items: [
                                        {
                                            xtype: 'combobox',
                                            fieldLabel: 'Авиакомпания',
                                            emptyText: 'Все',
                                            store: 'CarrierStore',
                                            displayField: 'name',
                                            valueField: 'id',
                                            bind: '{filter.carrierId}',
                                            listeners: {
                                                select: 'onFilterChanged'
                                            }
                                        },
                                        {
                                            xtype: 'button',
                                            type: 'reset',
                                            flex: 1,
                                            text: 'Сброс',
                                            handler: 'onResetButtonPressed'
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            xtype: 'gridpanel',
                            reference: 'terminalsGrid',
                            store: 'TerminalStore',
                            flex: 1,
                            height: 500,
                            layout: 'fit',
                            columnLines: true,
                            viewConfig: {
                                stripeRows: true
                            },
                            listeners: {
                                itemclick: 'onTerminalClick'
                            },
                            tools: [{
                                type: 'plus',
                                tooltip: 'Добавить терминал',
                                handler: 'onAddTerminalButtonPressed'
                            }
                            ],
                            columns: [
                                {
                                    xtype: 'gridcolumn',
                                    flex: 1,
                                    dataIndex: 'name',
                                    text: 'Название'
                                },
                                {
                                    xtype: 'gridcolumn',
                                    flex: 1,
                                    dataIndex: 'carrierId',
                                    text: 'Авиакомпания',
                                    renderer: function (carrierId) {
                                        return Ext.data.StoreManager.lookup('CarrierStore').getById(carrierId).get('name');
                                    }
                                },
                                {
                                    xtype: 'gridcolumn',
                                    flex: 1,
                                    dataIndex: 'agrn',
                                    text: 'AGRN'
                                },
                                {
                                    xtype: 'gridcolumn',
                                    flex: 1,
                                    dataIndex: 'terminal',
                                    text: 'терминал'
                                },
                                {
                                    xtype: 'gridcolumn',
                                    flex: 1,
                                    dataIndex: 'currenciesIds',
                                    text: 'Валюты',
                                    renderer: function (currenciesIds) {
                                        var store = Ext.data.StoreManager.lookup('CurrencyStore');
                                        var result = '';
                                        Ext.each(currenciesIds.sort(), function (currencyId) {
                                            result += store.getById(currencyId).get('countryCode') + ', ';
                                        });
                                        if (result.length > 0) {
                                            return result.substr(0, result.length - 2);  //убираем последнюю запятую
                                        } else {
                                            return '';
                                        }
                                    }
                                },
                                {
                                    xtype: 'actioncolumn',
                                    width: 30,
                                    items: [
                                        {
                                            icon: 'resources/img/minus.png',
                                            tooltip: 'Удалить терминал',
                                            handler: 'onRemoveTerminalButtonPressed'
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    title: 'Биллинговые системы',
                    items: [
                        {
                            xtype: 'gridpanel',
                            height: 500,
                            flex: 1,
                            store: 'BillingSystemStore',
                            viewConfig: {
                                stripeRows: true
                            },
                            tools: [{
                                type: 'plus',
                                tooltip: 'Добавить систему',
                                handler: 'onAddBillingSystemButtonPressed'
                            }
                            ],
                            columnLines: true,
                            listeners: {
                                itemclick: 'onBillingSystemClick'
                            },
                            columns: [
                                {
                                    xtype: 'gridcolumn',
                                    flex: 1,
                                    dataIndex: 'name',
                                    text: 'Название'
                                },
                                {
                                    xtype: 'gridcolumn',
                                    flex: 1,
                                    dataIndex: 'carrierId',
                                    renderer: function (carrierId) {
                                        var store = Ext.data.StoreManager.lookup('CarrierStore');
                                        var result = store.getById(carrierId).get('name');
                                        return carrierId + " " + result;
                                    },
                                    text: 'Авиакомпания'
                                },
                                {
                                    xtype: 'gridcolumn',
                                    flex: 1,
                                    dataIndex: 'createdDate',
                                    formatter: 'date("d-m-Y G:i:s")',
                                    text: 'Дата создания'
                                },
                                {
                                    xtype: 'gridcolumn',
                                    flex: 1,
                                    dataIndex: 'host',
                                    text: 'Хост'
                                },{
                                    xtype: 'gridcolumn',
                                    flex: 1,
                                    dataIndex: 'port',
                                    text: 'Порт'
                                },
                                {
                                    xtype: 'gridcolumn',
                                    flex: 1,
                                    dataIndex: 'path',
                                    text: 'Путь'
                                },
                                {
                                    xtype: 'gridcolumn',
                                    flex: 1,
                                    dataIndex: 'login',
                                    text: 'Логин'
                                },
                                {
                                    xtype: 'gridcolumn',
                                    flex: 1,
                                    dataIndex: 'password',
                                    text: 'Пароль'
                                },
                                {
                                    xtype: 'gridcolumn',
                                    flex: 1,
                                    dataIndex: 'emailsCSV',
                                    text: 'E-mails'
                                },
                                {
                                    xtype: 'gridcolumn',
                                    flex: 1,
                                    dataIndex: 'maskRegexp',
                                    text: 'Маска'
                                },
                                {
                                    xtype: 'gridcolumn',
                                    flex: 1,
                                    dataIndex: 'enabled',
                                    text: 'Включена'
                                }

                            ]
                        }
                    ]
                }
            ]
        }
    ]
});
