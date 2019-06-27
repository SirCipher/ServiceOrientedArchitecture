import React, {Component} from 'react';
import './App.css';
import {Route, Switch, withRouter} from 'react-router-dom';

import {getCurrentUser} from '../util/ApiHelper';
import {ACCESS_TOKEN} from '../constants';
import Home from '../home/Home';
import Notes from '../notes/Notes';
import Login from '../user/login/Login';
import Signup from '../user/signup/Signup';
import AppHeader from '../common/AppHeader';
import NotFound from '../common/NotFound';
import LoadingIndicator from '../common/LoadingIndicator';
import PrivateRoute from '../common/PrivateRoute';

import {Layout, notification} from 'antd';

const {Content} = Layout;

// Main application view
class App extends Component {
    constructor(props) {
        super(props);
        this.state = {
            currentUser: null,
            isAuthenticated: false,
            isLoading: false
        }
        this.handleLogout = this.handleLogout.bind(this);
        this.loadCurrentUser = this.loadCurrentUser.bind(this);
        this.handleLogin = this.handleLogin.bind(this);

        notification.config({
            placement: 'topRight',
            top: 70,
            duration: 3,
        });
    }

    loadCurrentUser() {
        this.setState({
            isLoading: true
        });
        getCurrentUser()
            .then(response => {
                this.setState({
                    currentUser: response,
                    isAuthenticated: true,
                    isLoading: false
                });
            }).catch(error => {
            this.setState({
                isLoading: false
            });
        });
    }

    componentWillMount() {
        this.loadCurrentUser();
    }

    handleLogout(redirectTo = "/", notificationType = "success", description = "Successfully logged out.") {
        localStorage.removeItem(ACCESS_TOKEN);

        this.setState({
            currentUser: null,
            isAuthenticated: false
        });

        this.props.history.push(redirectTo);

        notification[notificationType]({
            message: 'Nevernote',
            description: description,
        });
    }

    handleLogin() {
        notification.success({
            message: 'Nevernote',
            description: "Successfully logged in.",
        });
        this.loadCurrentUser();
        this.props.history.push("/notes/");
    }

    render() {
        if (this.state.isLoading) {
            return <LoadingIndicator/>
        }
        return (
            <Layout className="app-container">
                <AppHeader isAuthenticated={this.state.isAuthenticated}
                           currentUser={this.state.currentUser}
                           onLogout={this.handleLogout}/>

                <Content className="app-content">
                    <Switch>
                        <Route exact path="/" render={(props) => <Home/>}></Route>

                        <Route path="/login"
                               render={(props) => <Login onLogin={this.handleLogin} {...props} />}></Route>
                        <Route path="/signup" component={Signup}></Route>

                        <PrivateRoute authenticated={this.state.isAuthenticated} path="/notes" component={Notes}
                                      handleLogout={this.handleLogout}></PrivateRoute>

                        <Route component={NotFound}></Route>
                    </Switch>
                </Content>
            </Layout>
        );
    }
}

export default withRouter(App);