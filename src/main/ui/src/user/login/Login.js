import React, {Component} from 'react';
import {login} from '../../util/ApiHelper';
import './Login.css';
import {Link} from 'react-router-dom';
import {Button, Form, Icon, Input, Layout, notification} from 'antd';
import {ACCESS_TOKEN} from '../../constants';

const FormItem = Form.Item;

class Login extends Component {

    constructor(props) {
        super(props);

        // Remove any existing authorization token
        localStorage.removeItem(ACCESS_TOKEN);
    }

    render() {
        const AntWrappedLoginForm = Form.create()(LoginForm)
        return (
            <div className="login-container">
                <h1 className="page-title">Login</h1>
                <div className="login-content">
                    <AntWrappedLoginForm onLogin={this.props.onLogin}/>
                </div>
            </div>
        );
    }
}

class LoginForm extends Component {
    constructor(props) {
        super(props);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleSubmit(event) {
        event.preventDefault();
        this.props.form.validateFields((err, values) => {
            if (!err) {
                const loginRequest = Object.assign({}, values);
                login(loginRequest)
                    .then(response => {
                        this.props.onLogin();
                    }).catch(error => {
                    if (error.status === 401) {
                        notification.error({
                            message: 'Nevernote',
                            description: 'Username or password incorrect'
                        });
                    } else {
                        notification.error({
                            message: 'Nevernote',
                            description: error || 'Please try again.'
                        });
                    }
                });
            }
        });
    }

    render() {
        const {getFieldDecorator} = this.props.form;
        return (
            <Layout style={{minHeight: '100vh'}}>
                <Form onSubmit={this.handleSubmit} className="login-form">
                    <FormItem>
                        {getFieldDecorator('userName', {
                            rules: [{required: true, message: 'Username required'}],
                        })(
                            <Input
                                prefix={<Icon type="user"/>}
                                size="large"
                                name="userName"
                                placeholder="Username"/>
                        )}
                    </FormItem>
                    <FormItem>
                        {getFieldDecorator('password', {
                            rules: [{required: true, message: 'Password required'}],
                        })(
                            <Input
                                prefix={<Icon type="lock"/>}
                                size="large"
                                name="password"
                                type="password"
                                placeholder="Password"/>
                        )}
                    </FormItem>
                    <FormItem>
                        <Button type="primary" htmlType="submit" size="large"
                                className="login-form-button">Login</Button>
                        <Link to="/signup">Register</Link>
                    </FormItem>
                </Form>
            </Layout>
        );
    }
}


export default Login;