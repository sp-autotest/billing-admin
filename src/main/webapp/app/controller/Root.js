/**
 * This global controller manages the login view and ensures that view is created when
 * the application is launched. Once login is complete we then create the main view.
 */
Ext.define('BSP.controller.Root', {
    extend: 'Ext.app.Controller',
    
    requires: [
        'BSP.view.login.Login',
        'BSP.view.main.Main',
        'BSP.LoginManager',
        'BSP.view.admin.Admin'
    ],
    
    loadingText: 'Loading...',
    
    onLaunch: function () {
        if (Ext.isIE8) {
            Ext.Msg.alert('Not Supported', 'This example is not supported on Internet Explorer 8. Please use a different browser.');
            return;
        }
        
        this.session = new Ext.data.Session({
            autoDestroy: false
        });
        this.login = new BSP.view.login.Login({
            session: this.session,
            autoShow: true,
            listeners: {
                scope: this,
                login: 'onLogin'
            }
        });
    },

    /**
     * Called when the login controller fires the "login" event.
     *
     * @param loginController
     * @param user
     * @param organization
     * @param loginManager
     */
    onLogin: function (loginController, user, organization, loginManager) {
        this.login.destroy();

        this.loginManager = loginManager;
        this.organization = organization;
        this.user = user;

        this.showUI();
    },
    
    showUI: function() {
        this.viewport = new BSP.view.admin.Admin(
            {
                session: this.session,
                viewModel: {
                    data: {
                        currentUser: this.user
                    }
                }
            }
        );
    },
    
    getSession: function() {
        return this.session;
    }
});
