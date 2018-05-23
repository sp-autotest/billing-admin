Ext.define('BSP.view.main.Main', {
    extend: 'Ext.container.Viewport',

    xtype: 'app-admin',
    controller: 'main',
    requires: [
        'Ext.layout.container.Border'
    ],
    viewModel: {
        type: 'main'
    },

    layout: {
        type: 'border'
    },

    requires: [
        'BSP.view.main.MainModel',
        'BSP.view.main.MainController',
        'BSP.view.billing.Billing',
        'BSP.view.bo.Bo',
        'BSP.view.ticket.Ticket',
        'BSP.view.system.System',
        'BSP.view.carrier.Carriers',
        'Ext.tab.Panel',
        'Ext.form.field.Date',
        'Ext.form.field.File',
        'Ext.form.Panel',
        'Ext.form.RadioGroup',
        'Ext.form.field.Radio',
        'Ext.form.field.ComboBox',
        'Ext.tree.Panel',
        'Ext.tree.View',
        'Ext.tree.Column',
        'Ext.tab.Tab',
        'Ext.grid.Panel',
        'Ext.grid.column.Date',
        'Ext.grid.View',
        'BSP.view.automate.Automate'

    ],


    items: [
        {
            xtype: 'tabpanel',
            flex: 1,
            region: 'center',
            itemId: 'contentPanel',
            bind: {
                activeTab: {
                    bindTo: '1',
                    single: true
                }
            },
            items: [
                {
                    xtype: 'app-billing',
                    title: 'Биллинговые файлы',
                    itemId: 'tab1'
                },
                {
                    xtype: 'app-bo',
                    itemId: 'tab2',
                    title: 'БО файлы'
                },
                {
                    xtype: 'app-ticket',
                    itemId: 'tab3',
                    title: 'Билеты'
                },
                {
                    xtype: 'app-carriers',
                    itemsId: 'tab4',
                    title: 'Авиакомпании'
                },
                {
                    xtype: 'app-system',
                    itemsId: 'tab5',
                    title: 'Системная информация'
                },
                {
                    xtype: 'app-automate',
                    itemsId: 'tab6',
                    title: 'Настройки автоматизации'
                }
            ],
            listeners: {
                afterrender: function(panel) {
                    var bar = panel.tabBar;
                    bar.insert(6,[
                        {
                            xtype: 'component',
                            flex: 1
                        },
                        {
                            xtype: 'box',
                            autoEl: {tag: 'a', href: 'mvc/logout', html: 'Выход'}
                        }
                    ]);
                }
            }
        }

    ]


});
