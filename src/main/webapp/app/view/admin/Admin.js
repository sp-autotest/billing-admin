Ext.define('BSP.view.admin.Admin', {
    extend: 'Ext.container.Viewport',

    xtype: 'app-admin',
    controller: 'admin',
    requires: [
        'Ext.layout.container.Border'
    ],
    viewModel: {
        type: 'admin'
    },

    layout: {
        type: 'border'
    },

    requires: [
        'BSP.view.admin.AdminModel',
        'BSP.view.admin.AdminController',
        'BSP.view.billing.Billing',
        'BSP.view.bo.Bo',
        'BSP.view.ticket.Ticket',
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
        'Ext.grid.View'
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
                }
            ]
        }
    ]


});
