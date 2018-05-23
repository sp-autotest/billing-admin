Ext.define('BSP.view.admin.AdminController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.admin',

    requires: [
        'Ext.MessageBox'
    ],

    onClickButton: function () {
        Ext.Msg.confirm('Confirm', 'Are you sure?', 'onConfirm', this);
    }
});
