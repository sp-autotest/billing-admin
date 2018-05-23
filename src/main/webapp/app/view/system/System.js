Ext.define('BSP.view.system.System',{
    extend: 'Ext.panel.Panel',
    alias: 'widget.app-system',

    requires: [
        'BSP.view.system.SystemController',
        'Ext.grid.plugin.CellEditing'
    ],

    controller: 'system',

    padding: 5,
    layout: {
        header: false,
        type: 'border'
    },

    items: [
        {
            xtype: 'form',
            region: 'center',
            autoScroll: true,
            items: [
                {
                    xtype: 'gridpanel',
                    flex: 1,
                    title: 'Список пользователей',
                    reference: 'userGrid',
                    viewConfig: {
                        stripeRows: true
                    },
                    store: 'UserStore',
                    listeners: {
                        render: 'onShowUserGrid'
                    },
                    columns: [
                        {
                            xtype: 'gridcolumn',
                            flex: 1,
                            dataIndex : 'id',
                            text: 'ID'
                        },
                        {
                            xtype: 'gridcolumn',
                            flex: 1,
                            dataIndex : 'username',
                            text: 'Имя пользователя'
                        },
                        {
                            xtype: 'gridcolumn',
                            flex: 1,
                            dataIndex : 'updatedAt',
                            text: 'Дата изменения'
                        },
                        {
                            xtype: 'gridcolumn',
                            flex: 1,
                            dataIndex : 'credentialsExpiredAt',
                            text: 'Срок истечения пароля'
                        },
                        {
                            xtype: 'gridcolumn',
                            flex: 1,
                            dataIndex : 'isLocked',
                            text: 'Заблокирован'
                        },
                        {
                            xtype: 'gridcolumn',
                            flex: 1,
                            dataIndex : 'isAccountExpired',
                            text: 'Просрочен'
                        },
                        {
                            xtype: 'gridcolumn',
                            flex: 1,
                            dataIndex : 'isEnabled',
                            text: 'Доступен'
                        }
                    ]
                },
                {
                    xtype: 'gridpanel',
                    flex: 1,
                    title: 'Список системных обработчиков',
                    reference: 'systemGrid',
                    store: 'SystemStore',
                    listeners: {
                        render: 'onShowSystemGrid'
                    },
                    viewConfig: {
                        stripeRows: true
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
                            dataIndex : 'value',
                            text: 'Имя класса'
                        }
                    ]
                },
                {
                    xtype: 'gridpanel',
                    flex: 1,
                    title: 'Список системных переменных',
                    reference: 'systemSettingsGrid',
                    store: 'SystemSettingStore',
                    listeners: {
                        render: 'onShowSystemSettingsGrid'
                    },
                    viewConfig: {
                        stripeRows: true
                    },

                    // plugins: [
                    //     Ext.create('Ext.grid.plugin.CellEditing', {
                    //         clicksToEdit: 1,
                    //         listeners: {
                    //             edit : 'onEditSystemSettingsGrid'
                    //         }
                    //     })
                    // ],
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
                            dataIndex : 'value',
                            text: 'Значение',
                            editor : {
                                xtype: 'textfield',
                                allowBlank:false
                            }
                        }
                    ]
                }
           ]
        }
    ]
});
