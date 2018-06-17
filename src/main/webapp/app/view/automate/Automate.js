var stopTaskTip = 'Прерывает только текущую задачу. Не останавливает дальнейшее выполнение задачи по плану.';
var startTaskTip = 'Запускает выполнение задачи по плану. Задача запустится по расписанию.';
var savePlanTip = 'Обновляет расписание. Текущее выполнение не прерывается. Задача запустится по расписанию.';
var cronTip = '<html>' +
    'Date sequence generator for a <a href="http://www.manpagez.com/man/5/crontab/">Crontab pattern</a>,\n' +
    'allowing clients to specify a pattern that the sequence matches.\n' +
    '\n' +
    '<p>The pattern is a list of six single space-separated fields: representing\n' +
    'second, minute, hour, day, month, weekday. Month and weekday names can be\n' +
    'given as the first three letters of the English names.\n' +
    '\n' +
    '<p>Example patterns:\n' +
    '<ul>\n' +
    '<li>"0 0 * * * *" = the top of every hour of every day.</li>\n' +
    '<li>"*&#47;10 * * * * *" = every ten seconds.</li>\n' +
    '<li>"0 0 8-10 * * *" = 8, 9 and 10 o\'clock of every day.</li>\n' +
    '<li>"0 0/30 8-10 * * *" = 8:00, 8:30, 9:00, 9:30 and 10 o\'clock every day.</li>\n' +
    '<li>"0 0 9-17 * * MON-FRI" = on the hour nine-to-five weekdays</li>\n' +
    '<li>"0 0 0 25 12 ?" = every Christmas Day at midnight</li>\n' +
    '</ul>' +
    '</html>';

Ext.define('BSP.view.automate.Automate', {
        extend: 'Ext.panel.Panel',
        alias: 'widget.app-automate',

        requires: [
            'BSP.view.automate.AutomateController',
            'Ext.grid.plugin.CellEditing',
            'Ext.tip.ToolTip'
        ],
        controller: 'automate',
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
                        xtype: 'form',
                        width: '33%',
                        title: 'Глобальный статус автоматизации',
                        items: [


                            {
                                xtype: 'container',
                                margin: 5,
                                layout: {type: 'vbox', align: 'stretch'},
                                // width: '30%',
                                items: [
                                    {
                                        xtype: 'textfield',
                                        anchor: '100%',
                                        padding: '3px',
                                        fieldLabel: 'Текущий глобальный статус',
                                        readOnly: true,
                                        html: '-----',
                                        listeners: {
                                            render: 'getGlobalStatus'
                                        },
                                        id: 'currentAutomateStatus'

                                    }
                                ]
                            }
                        ]
                    }

                ]
            },
            /////////////////////////////////////////////////////////////
            {
                xtype: 'container',
                margin: '30px 0 5px',

                layout: {
                    type: 'hbox',
                    align: 'stretch'
                },
                items: [
                    {
                        xtype: 'form',
                        width: '100%',
                        titleAlign: 'center',
                        title: 'BSP-Posting автоматизация'
                    }]
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
                        width: '33%',
                        title: 'План запуска',
                        items: [
                            {

                                items: [
                                    {
                                        xtype: 'container',
                                        margin: 5,
                                        layout: {type: 'hbox', align: 'stretch'},
                                        items: [
                                            {
                                                xtype: 'button',
                                                text: 'Обновить план',
                                                handler: 'onRefreshBspPlanClick',
                                                tooltip: cronTip
                                            }
                                        ]
                                    },
                                    {
                                        xtype: 'container',
                                        margin: 5,
                                        layout: {type: 'vbox', align: 'stretch'},
                                        items: [
                                            {
                                                xtype: 'fieldset',
                                                defaultType: 'textfield',
                                                title: 'Текущий план:',
                                                listeners: {
                                                    render: 'onRefreshBspPlanClick'
                                                },
                                                items: [
                                                    {
                                                        xtype: 'textfield',
                                                        anchor: '100%',
                                                        fieldLabel: 'Cron',
                                                        padding: '3px',
                                                        value: '-----',
                                                        id: 'cronExpressionBSP'
                                                    },
                                                    {
                                                        xtype: 'textfield',
                                                        anchor: '100%',
                                                        fieldLabel: 'Next exec:',
                                                        padding: '3px',
                                                        value: '-----',
                                                        id: 'nextExecutionTimeBSP'
                                                    }
                                                ]
                                            }]
                                    },

                                    {
                                        xtype: 'container',
                                        margin: 5,
                                        layout: {type: 'hbox', align: 'stretch'},
                                        items: [
                                            {
                                                xtype: 'button',
                                                text: 'Сохранить план',
                                                tooltip: savePlanTip,
                                                margin: '0 10 0 0',
                                                handler: 'onSaveBspPlanClick'
                                            }]
                                    }
                                ]

                            }]
                    },
                    {
                        xtype: 'form',
                        width: '33%',

                        title: 'Статус задачи обработки BSP-Posting файлов',
                        items: [
                            {
                                xtype: 'container',
                                margin: 5,
                                layout: {type: 'hbox', align: 'stretch'},
                                items: [
                                    {
                                        xtype: 'button',
                                        text: 'Обновить статус',
                                        margin: '0 10 0 0',
                                        handler: 'onRefreshBspTaskClick'
                                    }]
                            },

                            {
                                xtype: 'container',
                                margin: 5,
                                layout: {type: 'vbox', align: 'stretch'},
                                items: [
                                    {
                                        // xtype: 'fieldset',
                                        // defaultType: 'datefield',
                                        // title: 'Текущий статус задачи:',
                                        // items: [
                                        //     {
                                        xtype: 'textfield',
                                        anchor: '100%',
                                        padding: '3px',
                                        fieldLabel: 'Текущий статус',
                                        readOnly: true,
                                        html: '-----',
                                        listeners: {
                                            render: 'onRefreshBspTaskClick'
                                        },
                                        id: 'currentTaskStatusBSP'

                                        // }
                                        // ]
                                    }]
                            },
                            {
                                xtype: 'container',
                                margin: 5,
                                layout: {type: 'hbox', align: 'stretch'},
                                items: [
                                    {
                                        xtype: 'button',
                                        text: 'Остановить',
                                        tooltip: stopTaskTip,
                                        margin: '0 10 0 0',
                                        handler: 'onStopBspTaskClick'
                                    }]
                            }


                        ]
                    },
                    {
                        xtype: 'form',
                        width: '33%',

                        title: 'Параметры сервера Posting файлов',
                        items: [
                            {
                                xtype: 'container',
                                margin: 5,
                                layout: {type: 'hbox', align: 'stretch'},
                                items: [
                                    {
                                        xtype: 'button',
                                        text: 'Обновить',
                                        margin: '0 10 0 0',
                                        handler: 'onUpdatePostingServerClick'
                                    },
                                    {
                                        xtype: 'button',
                                        text: 'Сохранить',
                                        margin: '0 10 0 0',
                                        handler: 'onSavePostingServerClick'
                                    }
                                ]
                            },

                            {
                                xtype: 'container',
                                margin: 5,
                                layout: {type: 'vbox', align: 'stretch'},
                                items: [
                                    {
                                        xtype: 'fieldset',
                                        defaultType: 'datefield',
                                        title: 'Параметры:',
                                        listeners: {
                                            render:
                                                'onUpdatePostingServerClick'
                                        },
                                        items: [
                                            {
                                                layout: 'hbox',
                                                xtype: "container",
                                                items: [
                                                    {
                                                        xtype: 'textfield',
                                                        labelWidth: '100px',
                                                        padding: '3px',
                                                        fieldLabel: 'Адрес',
                                                        id: 'postingAddress',
                                                        html: '-----'
                                                    }, {
                                                        xtype: 'textfield',
                                                        labelWidth: '30px',
                                                        padding: '3px',
                                                        fieldLabel: 'Порт',
                                                        id: 'postingPort',
                                                        html: '-----'
                                                    }
                                                ]
                                            },
                                            {
                                                layout: 'hbox',
                                                xtype: 'container',
                                                items: [
                                                    {
                                                        xtype: 'textfield',
                                                        padding: '3px',
                                                        labelWidth: '100px',
                                                        fieldLabel: 'Логин',
                                                        id: 'postingLogin',
                                                        html: '-----'
                                                    },
                                                    {
                                                        xtype: 'textfield',
                                                        labelWidth: '100px',
                                                        padding: '3px',
                                                        fieldLabel: 'Пароль',
                                                        id: 'postingPassword',
                                                        html: '-----'
                                                    }
                                                ]
                                            },
                                            {
                                                layout: 'hbox',
                                                xtype: 'container',
                                                items: [

                                                    {
                                                        xtype: 'textfield',
                                                        padding: '3px',
                                                        labelWidth: '100px',
                                                        fieldLabel: 'Путь',
                                                        id: 'postingPath',
                                                        html: '-----'
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
            },
            /////////////////////////////////////////////////////////////
            {
                xtype: 'container',
                margin: '30px 0 5px',

                layout: {
                    type: 'hbox',
                    align: 'stretch'
                },
                items: [
                    {
                        xtype: 'form',
                        width: '100%',
                        titleAlign: 'center',
                        title: 'BO автоматизация'
                    }]
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
                        width: '33%',
                        title: 'План запуска',
                        // margin: 5,
                        items: [
                            {
                                items: [
                                    {
                                        xtype: 'container',
                                        margin: 5,
                                        layout: {type: 'hbox', align: 'stretch'},
                                        items: [
                                            {
                                                xtype: 'button',
                                                text: 'Обновить план',
                                                handler: 'onRefreshBoPlanClick'
                                            }
                                        ]
                                    },
                                    {
                                        xtype: 'container',
                                        margin: 5,
                                        layout: {type: 'vbox', align: 'stretch'},
                                        items: [
                                            {
                                                xtype: 'fieldset',
                                                defaultType: 'textfield',
                                                title: 'Текущий план:',
                                                listeners: {
                                                    render: 'onRefreshBoPlanClick'
                                                },
                                                items: [
                                                    {
                                                        xtype: 'textfield',
                                                        anchor: '100%',
                                                        fieldLabel: 'Cron',
                                                        padding: '3px',
                                                        value: '-----',
                                                        id: 'cronExpressionBo'
                                                    },
                                                    {
                                                        xtype: 'textfield',
                                                        anchor: '100%',
                                                        fieldLabel: 'Next exec:',
                                                        padding: '3px',
                                                        value: '-----',
                                                        id: 'nextExecutionTimeBO'
                                                    }
                                                ]
                                            }]
                                    },
                                    {
                                        xtype: 'container',
                                        margin: 5,
                                        layout: {type: 'hbox', align: 'stretch'},
                                        items: [
                                            {
                                                xtype: 'button',
                                                text: 'Сохранить план',
                                                tooltip: savePlanTip,
                                                margin: '0 10 0 0',
                                                handler: 'onSaveBoPlanClick'
                                            }]
                                    }
                                ]
                            }]
                    },
                    {
                        xtype: 'form',
                        width: '33%',
                        title: 'Статус задачи обработки Bo файлов',
                        items: [
                            {
                                xtype: 'container',
                                margin: 5,
                                layout: {type: 'hbox', align: 'stretch'},
                                items: [
                                    {
                                        xtype: 'button',
                                        text: 'Обновить статус',
                                        margin: '0 10 0 0',
                                        handler: 'onRefreshBoTaskClick'
                                    }]
                            },

                            {
                                xtype: 'container',
                                margin: 5,
                                layout: {type: 'vbox', align: 'stretch'},
                                items: [
                                    {
                                        xtype: 'fieldset',
                                        defaultType: 'datefield',
                                        title: 'Текущий статус задачи:',
                                        items: [
                                            {
                                                xtype: 'textfield',
                                                anchor: '100%',
                                                padding: '3px',
                                                fieldLabel: 'Текущий статус',
                                                readOnly: true,
                                                html: '-----',
                                                listeners: {
                                                    render: 'onRefreshBoTaskClick'
                                                },
                                                id: 'currentTaskStatusBo'

                                            }
                                        ]
                                    }]
                            },
                            {
                                xtype: 'container',
                                margin: 5,
                                layout: {type: 'hbox', align: 'stretch'},
                                items: [
                                    {
                                        xtype: 'button',
                                        text: 'Остановить',
                                        margin: '0 10 0 0',
                                        tooltip: stopTaskTip,
                                        handler: 'onStopBoTaskClick'
                                    }]
                            }


                        ]
                    }
                    ,
                    {
                        xtype: 'form',
                        width: '33%',

                        title: 'Параметры сервера Bo файлов',
                        items: [
                            {
                                xtype: 'container',
                                margin: 5,
                                layout: {type: 'hbox', align: 'stretch'},
                                items: [
                                    {
                                        xtype: 'button',
                                        text: 'Обновить',
                                        margin: '0 10 0 0',
                                        handler: 'onUpdateBoServerClick'
                                    },
                                    {
                                        xtype: 'button',
                                        text: 'Сохранить',
                                        margin: '0 10 0 0',
                                        handler: 'onSaveBoServerClick'
                                    }
                                ]
                            },

                            {
                                xtype: 'container',
                                margin: 5,
                                layout: {type: 'vbox', align: 'stretch'},
                                items: [
                                    {
                                        xtype: 'fieldset',
                                        defaultType: 'datefield',
                                        title: 'Параметры:',
                                        listeners: {
                                            render:
                                                'onUpdateBoServerClick'
                                        },
                                        items: [
                                            {
                                                layout: 'hbox',
                                                xtype: "container",
                                                items: [
                                                    {
                                                        xtype: 'textfield',
                                                        labelWidth: '100px',
                                                        padding: '3px',
                                                        fieldLabel: 'Адрес',
                                                        id: 'boAddress',
                                                        html: '-----'
                                                    }, {
                                                        xtype: 'textfield',
                                                        labelWidth: '30px',
                                                        padding: '3px',
                                                        fieldLabel: 'Порт',
                                                        id: 'boPort',
                                                        html: '-----'
                                                    }
                                                ]
                                            },
                                            {
                                                layout: 'hbox',
                                                xtype: 'container',
                                                items: [
                                                    {
                                                        xtype: 'textfield',
                                                        padding: '3px',
                                                        labelWidth: '100px',
                                                        fieldLabel: 'Логин',
                                                        id: 'boLogin',
                                                        html: '-----'
                                                    },
                                                    {
                                                        xtype: 'textfield',
                                                        labelWidth: '100px',
                                                        padding: '3px',
                                                        fieldLabel: 'Пароль',
                                                        id: 'boPassword',
                                                        html: '-----'
                                                    }
                                                ]
                                            },
                                            {
                                                layout: 'hbox',
                                                xtype: 'container',
                                                items: [
                                                    {
                                                        xtype: 'textfield',
                                                        padding: '3px',
                                                        labelWidth: '100px',
                                                        fieldLabel: 'Путь',
                                                        id: 'boPath',
                                                        html: '-----'
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
    }
);
