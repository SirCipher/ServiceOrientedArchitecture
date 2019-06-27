import React, {Component} from 'react';
import {Layout} from 'antd'
import {Text} from 'react-native'
import './Home.css';

class Home extends Component {
    render() {
        return (
            <Layout style={{minHeight: '100vh'}}>
                <Text style={{
                    textAlign: 'center',
                    fontSize: 36,
                }}>
                    Welcome to Nevernote
                </Text>
            </Layout>
        );
    }
}

export default Home;