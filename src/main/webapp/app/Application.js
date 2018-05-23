/**
 * The main application class. An instance of this class is created by app.js when it calls
 * Ext.application(). This is the ideal place to handle application launch and initialization
 * details.
 */
Ext.define('BSP.Application', {
    extend: 'Ext.app.Application',
    
    name: 'BSP',

    requires: [
        'Ext.app.bindinspector.*'
    ],

    controllers: [
        //'Root@BSP.controller'
    ],

    onBeforeLaunch: function () {
        this.callParent();
    },

    launch: function () {
        // TODO - Launch the application
    }
});
