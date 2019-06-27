import React from 'react';
import {Layout, Tree} from 'antd';
import {Button, Text} from 'react-native'
import {getNotebooks} from '../util/ApiHelper';
import LoadingIndicator from '../common/LoadingIndicator';

const {TreeNode} = Tree;

// Displays all notes in a tree format
class NoteList extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            isLoading: false,
            books: []
        }

        this.loadData = this.loadData.bind(this);
    }

    loadData() {
        this.setState({
            isLoading: true
        });

        getNotebooks().then(response => {
            if (this._isMounted) {
                this.setState({
                    books: response
                });
            }
        }).catch(error => {
            notification.error({
                message: 'Nevernote',
                description: error || 'Error fetching latest notebooks. Please try again'
            });
        });

        this.setState({
            isLoading: false
        })
    }

    componentWillUnmount() {
        this._isMounted = false;
    }

    componentDidMount() {
        this._isMounted = true;
        this.loadData();
    }

    // Generates the tree by note/notebooks
    generateTree = data => data.map((item) => {
        if (item.notes) {
            if (item.notes.length > 0) {
                return (
                    // Current element is a ntoe
                    <TreeNode title={item.title} key={item.id} dataRef={item}>
                        {this.generateTree(item.notes)}
                    </TreeNode>
                )
            }
        } else {
            // It's a note
            item.isLeaf = true;
        }

        return <TreeNode {...item} key={item.id} dataRef={item}/>;
    })

    // Callback to the class that is using this list
    onSelect(selectedKeys, info) {
        // If it is a note
        // [0] as multi-selection is disabled        
        if (info.selectedNodes[0]) {
            var data = info.selectedNodes[0].props.dataRef;

            if (data.isLeaf) {
                this.props.onEvent(data);
            } else {
                this.props.onClose();
            }
        }
    }

    render() {
        if (this.state.isLoading) {
            return <LoadingIndicator/>
        }
        return (
            <Layout style={{maxWidth: '25vh', paddingTop: '10px', backgroundColor: '#FFFFFF'}}>
                <Text style={{textAlign: 'center', fontSize: 18}}>
                    My Notes
                </Text>
                <Tree
                    showLine
                    onSelect={this.onSelect.bind(this)}>
                    {this.generateTree(this.state.books)}
                </Tree>
            </Layout>
        );
    }
}

export default NoteList;