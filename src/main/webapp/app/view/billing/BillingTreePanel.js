Ext.define('BSP.view.billing.BillingTreePanel',{
    alias : 'widget.BillingTreePanel',
    extend: 'Ext.tree.Panel',
    requires: [
        'Ext.data.*',
        'Ext.grid.*',
        'Ext.tree.*',
        'Ext.ux.CheckColumn',
        'BSP.model.BillingFile'
    ],

    height: 250,
    width: 400,
    title: 'Список файлов',
    xtype: 'billing-treepanel',
    //store: 'billingFile',
    columns: [
        {
            xtype: 'treecolumn',
            dataIndex: 'id',
            text: 'ID',
            flex: 1
        },
        {
            xtype: 'gridcolumn',
            dataIndex: 'fileName',
            text: 'Имя файла'
        },
        {
            xtype: 'gridcolumn',
            text: 'Тип файла',
            dataIndex: 'fileType'
        },
        {
            xtype: 'gridcolumn',
            text: 'Отчётная дата',
            dataIndex: 'processingDate'
        },
        {
            xtype: 'gridcolumn',
            text: 'Дата создания',
            dataIndex: 'createdDate'
        },
        {
            xtype: 'gridcolumn',
            text: 'Формат',
            dataIndex: 'format'
        }
    ]

});
