import React, {Component} from 'react';
import {emailAvailable, signup, usernameAvailable} from '../../util/ApiHelper';
import './Signup.css';
import {Link} from 'react-router-dom';
import {
    ACCESS_TOKEN,
    EMAIL_MAXIMUM_LENGTH,
    NAME_MAXIMUM_LENGTH,
    NAME_MINIMUM_LENGTH,
    PASSWORD_MAXIMUM_LENGTH,
    PASSWORD_MINIMUM_LENGTH,
    USERNAME_MAXIMUM_LENGTH,
    USERNAME_MINIMUM_LENGTH
} from '../../constants';

import {Button, Form, Input, Layout, notification} from 'antd';

const FormItem = Form.Item;

class Signup extends Component {
    constructor(props) {
        super(props);
        this.state = {
            firstName: {
                value: ''
            },
            lastName: {
                value: ''
            },
            userName: {
                value: ''
            },
            emailAddress: {
                value: ''
            },
            password: {
                value: ''
            }
        }

        // Remove any existing authorization token
        localStorage.removeItem(ACCESS_TOKEN);

        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.usernameAvailable = this.usernameAvailable.bind(this);
        this.emailAvailable = this.emailAvailable.bind(this);
        this.isFormInvalid = this.isFormInvalid.bind(this);
    }

    handleInputChange(event, validationFun) {
        const target = event.target;
        const inputName = target.name;
        const inputValue = target.value;

        this.setState({
            [inputName]: {
                value: inputValue,
                ...validationFun(inputValue)
            }
        });
    }

    handleSubmit(event) {
        event.preventDefault();

        const signupRequest = {
            firstName: this.state.firstName.value,
            lastName: this.state.lastName.value,
            emailAddress: this.state.emailAddress.value,
            userName: this.state.userName.value,
            password: this.state.password.value
        };

        signup(signupRequest)
            .then(response => {
                notification.success({
                    message: 'Nevernote',
                    description: "Please login to continue!",
                });
                this.props.history.push("/login");
            }).catch(error => {
            notification.error({
                message: 'Nevernote',
                description: error || 'Please try again.'
            });
        });
    }

    isFormInvalid() {
        return !(
            this.state.firstName.validateStatus === 'success' &&
            this.state.lastName.validateStatus === 'success' &&
            this.state.userName.validateStatus === 'success' &&
            this.state.emailAddress.validateStatus === 'success' &&
            this.state.password.validateStatus === 'success'
        );
    }

    render() {
        return (
            <Layout style={{minHeight: '100vh'}}>
                <div className="signup-container">
                    <h1 className="page-title">Sign Up For Nevernote</h1>
                    <div className="signup-content">
                        <Form onSubmit={this.handleSubmit} className="signup-form">
                            <FormItem
                                label="First Name"
                                validateStatus={this.state.firstName.validateStatus}
                                help={this.state.firstName.errorMsg}>
                                <Input
                                    size="large"
                                    name="firstName"
                                    autoComplete="off"
                                    placeholder="Your first name"
                                    value={this.state.firstName.value}
                                    onChange={(event) => this.handleInputChange(event, this.validateName)}/>
                            </FormItem>
                            <FormItem
                                label="Last Name"
                                validateStatus={this.state.lastName.validateStatus}
                                help={this.state.lastName.errorMsg}>
                                <Input
                                    size="large"
                                    name="lastName"
                                    autoComplete="off"
                                    placeholder="Your last name"
                                    value={this.state.lastName.value}
                                    onChange={(event) => this.handleInputChange(event, this.validateName)}/>
                            </FormItem>
                            <FormItem label="Username"
                                      hasFeedback
                                      validateStatus={this.state.userName.validateStatus}
                                      help={this.state.userName.errorMsg}>
                                <Input
                                    size="large"
                                    name="userName"
                                    autoComplete="off"
                                    placeholder="Your username"
                                    value={this.state.userName.value}
                                    onBlur={this.usernameAvailable}
                                    onChange={(event) => this.handleInputChange(event, this.validateUsername)}/>
                            </FormItem>
                            <FormItem
                                label="Email Address"
                                hasFeedback
                                validateStatus={this.state.emailAddress.validateStatus}
                                help={this.state.emailAddress.errorMsg}>
                                <Input
                                    size="large"
                                    name="emailAddress"
                                    type="emailAddress"
                                    autoComplete="off"
                                    placeholder="Your email address"
                                    value={this.state.emailAddress.value}
                                    onBlur={this.emailAvailable}
                                    onChange={(event) => this.handleInputChange(event, this.validateEmail)}/>
                            </FormItem>
                            <FormItem
                                label="Password"
                                validateStatus={this.state.password.validateStatus}
                                help={this.state.password.errorMsg}>
                                <Input
                                    size="large"
                                    name="password"
                                    type="password"
                                    autoComplete="off"
                                    placeholder="A password between 6 to 20 characters"
                                    value={this.state.password.value}
                                    onChange={(event) => this.handleInputChange(event, this.validatePassword)}/>
                            </FormItem>
                            <FormItem>
                                <Button type="primary"
                                        htmlType="submit"
                                        size="large"
                                        className="signup-form-button"
                                        disabled={this.isFormInvalid()}>Sign up</Button>
                                <Link to="/login">Login</Link>
                            </FormItem>
                        </Form>
                    </div>
                </div>
            </Layout>
        );
    }

    // Validation Functions

    validateName = (name) => {
        if (name.length < NAME_MINIMUM_LENGTH) {
            return {
                validateStatus: 'error',
                errorMsg: `Name must be at least ${NAME_MINIMUM_LENGTH} characters.`
            }
        } else if (name.length > NAME_MAXIMUM_LENGTH) {
            return {
                validationStatus: 'error',
                errorMsg: `Maximum name length ${NAME_MAXIMUM_LENGTH} characters`
            }
        } else {
            return {
                validateStatus: 'success',
                errorMsg: null,
            };
        }
    }

    validateEmail = (emailAddress) => {
        if (!emailAddress) {
            return {
                validateStatus: 'error',
                errorMsg: 'Email may not be empty'
            }
        }

        const EMAIL_REGEX = RegExp('[^@ ]+@[^@ ]+\\.[^@ ]+');
        if (!EMAIL_REGEX.test(emailAddress)) {
            return {
                validateStatus: 'error',
                errorMsg: 'Email must be in the format of username@domain.something'
            }
        }

        if (emailAddress.length > EMAIL_MAXIMUM_LENGTH) {
            return {
                validateStatus: 'error',
                errorMsg: `Maximum email is ${EMAIL_MAXIMUM_LENGTH} characters.`
            }
        }

        return {
            validateStatus: null,
            errorMsg: null
        }
    }

    validateUsername = (userName) => {
        if (userName.length < USERNAME_MINIMUM_LENGTH) {
            return {
                validateStatus: 'error',
                errorMsg: `Username must be at least ${USERNAME_MINIMUM_LENGTH} characters.`
            }
        } else if (userName.length > USERNAME_MAXIMUM_LENGTH) {
            return {
                validationStatus: 'error',
                errorMsg: `Username length limit is ${USERNAME_MAXIMUM_LENGTH} characters.`
            }
        } else {
            return {
                validateStatus: null,
                errorMsg: null
            }
        }
    }

    usernameAvailable() {
        const usernameValue = this.state.userName.value;
        const usernameValidation = this.validateUsername(usernameValue);

        if (usernameValidation.validateStatus === 'error') {
            this.setState({
                userName: {
                    value: usernameValue,
                    ...usernameValidation
                }
            });
            return;
        }

        this.setState({
            userName: {
                value: usernameValue,
                validateStatus: 'validating',
                errorMsg: null
            }
        });

        usernameAvailable(usernameValue)
            .then(response => {
                if (response.success) {
                    this.setState({
                        userName: {
                            value: usernameValue,
                            validateStatus: 'success',
                            errorMsg: null
                        }
                    });
                } else {
                    this.setState({
                        userName: {
                            value: usernameValue,
                            validateStatus: 'error',
                            errorMsg: 'Username unavailable. Please choose another'
                        }
                    });
                }
            }).catch(error => {
            this.setState({
                userName: {
                    value: usernameValue,
                    validateStatus: 'success',
                    errorMsg: null
                }
            });
        });
    }

    emailAvailable() {
        const emailAddressValue = this.state.emailAddress.value;
        const emailAddressValidation = this.validateEmail(emailAddressValue);

        if (emailAddressValidation.validateStatus === 'error') {
            this.setState({
                emailAddress: {
                    value: emailAddressValue,
                    ...emailAddressValidation
                }
            });
            return;
        }

        this.setState({
            emailAddress: {
                value: emailAddressValue,
                validateStatus: 'validating',
                errorMsg: null
            }
        });

        emailAvailable(emailAddressValue)
            .then(response => {
                if (response.success) {
                    this.setState({
                        emailAddress: {
                            value: emailAddressValue,
                            validateStatus: 'success',
                            errorMsg: null
                        }
                    });
                } else {
                    this.setState({
                        emailAddress: {
                            value: emailAddressValue,
                            validateStatus: 'error',
                            errorMsg: 'Email address unavailable'
                        }
                    });
                }
            }).catch(error => {
            // Marking validateStatus as success, Form will be recchecked at server
            this.setState({
                emailAddress: {
                    value: emailAddressValue,
                    validateStatus: 'success',
                    errorMsg: null
                }
            });
        });
    }

    validatePassword = (password) => {
        if (password.length < PASSWORD_MINIMUM_LENGTH) {
            return {
                validateStatus: 'error',
                errorMsg: `Password is too short (Minimum ${PASSWORD_MINIMUM_LENGTH} characters needed.)`
            }
        } else if (password.length > PASSWORD_MAXIMUM_LENGTH) {
            return {
                validationStatus: 'error',
                errorMsg: `Password is too long (Maximum ${PASSWORD_MAXIMUM_LENGTH} characters allowed.)`
            }
        } else {
            return {
                validateStatus: 'success',
                errorMsg: null,
            };
        }
    }

}

export default Signup;