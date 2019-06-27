import React from 'react';
import NoteList from './NoteList'
import {Breadcrumb, Form, Icon, Input, Layout, Menu, Modal, notification, Select} from 'antd';
import Editor from './Editor';
import {Text} from 'react-native'
import {
    createNote,
    createNotebook,
    createNotification,
    deleteNote,
    deleteNotebook,
    getNote,
    getNotebooks,
    saveNote,
    shareNotebook,
    starNote,
    unshareNotebook
} from '../util/ApiHelper';
import LoadingIndicator from '../common/LoadingIndicator';

const FormItem = Form.Item;
const Option = Select.Option;
const confirm = Modal.confirm;

const {
    Header, Content, Sider,
} = Layout;
const SubMenu = Menu.SubMenu;

// New note modal
const NewNoteForm = Form.create({name: 'note_form'})(
    class extends React.Component {
        render() {
            const {
                visible, onCancel, onCreate, form, radioContents
            } = this.props;
            const {getFieldDecorator} = form;
            return (
                <Modal
                    visible={visible}
                    title="Create a new note"
                    okText="Create"
                    onCancel={onCancel}
                    onOk={onCreate}
                >
                    <Form layout="vertical">
                        <Form.Item label="Title">
                            {getFieldDecorator('title', {
                                rules: [{required: true, message: 'Title is required'}],
                            })(
                                <Input/>
                            )}
                        </Form.Item>
                        <Form.Item label="Notebook">
                            {getFieldDecorator('notebook', {
                                rules: [{required: true, message: 'Notebook is required'}],
                            })(
                                <Select>
                                    {radioContents}
                                </Select>
                            )}
                        </Form.Item>
                    </Form>
                </Modal>
            );
        }
    }
);

// Delete notebook modal
const DeleteNotebookForm = Form.create({name: 'delete_form'})(
    class extends React.Component {
        render() {
            const {
                visible, onCancel, onCreate, form, radioContents
            } = this.props;
            const {getFieldDecorator} = form;
            return (
                <Modal
                    visible={visible}
                    title="Delete a notebook"
                    okText="Delete"
                    onCancel={onCancel}
                    onOk={onCreate}
                >
                    <Form layout="vertical">
                        <Form.Item label="Notebook">
                            {getFieldDecorator('notebook', {
                                rules: [{required: true, message: 'Notebook is required'}],
                            })(
                                <Select>
                                    {radioContents}
                                </Select>
                            )}
                        </Form.Item>
                    </Form>
                </Modal>
            );
        }
    }
);

// New notebook modal
const NewNoteBookForm = Form.create({name: 'notebook_form'})(
    class extends React.Component {
        render() {
            const {
                visible, onCancel, onCreate, form, radioContents
            } = this.props;
            const {getFieldDecorator} = form;
            return (
                <Modal
                    visible={visible}
                    title="Create a new notebook"
                    okText="Create"
                    onCancel={onCancel}
                    onOk={onCreate}
                >
                    <Form layout="vertical">
                        <Form.Item label="Title">
                            {getFieldDecorator('title', {
                                rules: [{required: true, message: 'Title is required'}],
                            })(
                                <Input/>
                            )}
                        </Form.Item>
                    </Form>
                </Modal>
            );
        }
    }
);

// Share notebook modal
const ShareNoteForm = Form.create({name: 'share_note_form'})(
    class extends React.Component {
        render() {
            const {
                visible, onCancel, onCreate, form, radioContents
            } = this.props;
            const {getFieldDecorator} = form;
            return (
                <Modal
                    visible={visible}
                    title="Share a notebook"
                    okText="Share"
                    onCancel={onCancel}
                    onOk={onCreate}
                >
                    <Form layout="vertical">
                        <Form.Item label="Email address to share with">
                            {getFieldDecorator('email', {
                                rules: [{required: true, message: 'Email address required'}],
                            })(
                                <Input/>
                            )}
                        </Form.Item>
                        <Form.Item label="Access Level">
                            {getFieldDecorator('accessLevel', {
                                rules: [{required: true, message: 'Access level is required'}],
                            })(
                                <Select>
                                    <Option value="read">Read</Option>
                                    <Option value="write">Write</Option>
                                </Select>
                            )}
                        </Form.Item>
                        <Form.Item label="Notebook">
                            {getFieldDecorator('notebook', {
                                rules: [{required: true, message: 'Notebook is required'}],
                            })(
                                <Select>
                                    {radioContents}
                                </Select>
                            )}
                        </Form.Item>
                    </Form>
                </Modal>
            );
        }
    }
);

// Unshare notebook modal
const UnShareNoteForm = Form.create({name: 'unshare_note_form'})(
    class extends React.Component {
        render() {
            const {
                visible, onCancel, onCreate, form, radioContents
            } = this.props;
            const {getFieldDecorator} = form;
            return (
                <Modal
                    visible={visible}
                    title="Unshare a notebook"
                    okText="Unshare"
                    onCancel={onCancel}
                    onOk={onCreate}
                >
                    <Form layout="vertical">
                        <Form.Item label="Notebook">
                            {getFieldDecorator('notebook', {
                                rules: [{required: true, message: 'Notebook is required'}],
                            })(
                                <Select>
                                    {radioContents}
                                </Select>
                            )}
                        </Form.Item>
                    </Form>
                </Modal>
            );
        }
    }
);

// Slider class configuration
class SiderComponent extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            isLoading: false,
            newNoteModal: false,
            shareNoteModal: false,
            unShareNoteModal: false,
            newNoteBookModal: false,
            deleteNoteBookModal: false,
            collapsed: false,
            htmlLoaded: false,
            noteHtml: '',
            noteTitle: '',
            notebookTitle: '',
            readOnly: false,
            activeNote: {},
            bookList: [],
            modelDeleteVisible: false,
            newNoteForm: {
                title: {
                    value: ''
                },
                notebookId: {
                    value: undefined
                }
            }
        };

        this.loadData = this.loadData.bind(this);
    }


    onCollapse = (collapsed) => {
        this.setState({collapsed});
    }

    // Display HTML in editor
    updateEditor(e) {
        var c = this;

        this.setState({
            ...this.state,
            activeNote: e
        });

        getNote(e.id).then(response => {
            c.setState({
                ...c.state,
                editorHtml: response.html,
                htmlLoaded: true,
                readOnly: (response.accessLevel === "READ")
            })
        }).catch(error => {
            // TODO
        });
    }

    // Save the current editor state
    saveEditorHtml(e) {
        this.setState({
            ...this.state,
            editorHtml: e
        })
    }

    // Update editor HTML to server
    saveNote() {
        // No note loaded
        if (!this.state.activeNote) {
            return;
        }

        var n = {
            "id": this.state.activeNote.id,
            "html": this.state.editorHtml,
            "notebookName": this.state.notebookTitle
        }

        saveNote(n).then(response => {
            notification.info({
                message: 'Nevernote',
                description: 'Saved'
            });
        }).catch(error => {
            notification.error({
                message: 'Nevernote',
                description: error || 'Error saving. Please try again.'
            });
        })
    }

    showNewNoteModal() {
        this.setState({newNoteModal: true})
    }

    showUnshareNoteModal() {
        this.setState({unShareNoteModal: true});
    }

    showShareNoteModal() {
        this.setState({shareNoteModal: true});
    }

    showNewNoteBookModal() {
        this.setState({newNoteBookModal: true})
    }

    showDeleteNotebookModal() {
        this.setState({deleteNoteBookModal: true})
    }

    componentDidMount() {
        this.loadData();
    }

    handleCancelNote = () => {
        this.setState({newNoteModal: false});
    }

    handleDeleteCancelNotebook = () => {
        this.setState({deleteNoteBookModal: false});
    }

    handleCancelNotebook = () => {
        this.setState({newNoteBookModal: false});
    }

    handleCancelShare = () => {
        this.setState({shareNoteModal: false});
    }

    handleCancelUnshare = () => {
        this.setState({unShareNoteModal: false});
    }

    // Call to server to save the new notebook
    handleCreateNotebook = () => {
        const form = this.notebookFormRef.props.form;
        form.validateFields((err, values) => {
            if (err) {
                return;
            }

            createNotebook(values.title).then(response => {
                notification.info({
                    message: 'Nevernote',
                    description: 'Notebook created'
                });
                window.location.reload();
            }).catch(error => {
                notification.error({
                    message: 'Nevernote',
                    description: error || 'Error creating. Please try again.'
                });
            });

            form.resetFields();
            this.setState({newNoteBookModal: false});
        });
    }

    // Call to server to save the new note
    handleCreateNote = () => {
        const form = this.noteFormRef.props.form;
        form.validateFields((err, values) => {
            if (err) {
                return;
            }

            var n = {
                "id": null,
                "html": null,
                "notebookName": values.title
            }

            createNote(n, values.notebook).then(response => {
                notification.info({
                    message: 'Nevernote',
                    description: 'Saved'
                });
                window.location.reload();
            }).catch(error => {
                notification.error({
                    message: 'Nevernote',
                    description: error || 'Error saving. Please try again.'
                });
            })

            form.resetFields();
            this.setState({newNoteModal: false});
        });
    }

    // Call to server to delete the notebook
    handleDeleteNotebook = () => {
        const form = this.deleteNotebookFormReference.props.form;
        form.validateFields((err, values) => {
            if (err) {
                return;
            }

            deleteNotebook(values.notebook).then(response => {
                notification.info({
                    message: 'Nevernote',
                    description: 'Deleted'
                });
                window.location.reload();
            }).catch(error => {
                notification.error({
                    message: 'Nevernote',
                    description: error || 'Error deleting. Please try again.'
                });
            })

            form.resetFields();
            this.setState({newNoteModal: false});
        });
    }

    // Call to server to share the notebook
    handleShareNote = () => {
        const form = this.shareFormRef.props.form;
        form.validateFields((err, values) => {
            if (err) {
                return;
            }

            shareNotebook(values.email, values.accessLevel, values.notebook).then(response => {
                notification.info({
                    message: 'Nevernote',
                    description: 'Share'
                });
                window.location.reload();
            }).catch(error => {
                notification.error({
                    message: 'Nevernote',
                    description: error || 'Error sharing. Please try again.'
                });
            })

            form.resetFields();
            this.setState({shareNoteModal: false});
        });
    }

    // Call to server to unshare a notebook
    handleUnshareNote = () => {
        const form = this.unShareFormRef.props.form;
        form.validateFields((err, values) => {
            if (err) {
                return;
            }

            unshareNotebook(values.notebook).then(response => {
                notification.info({
                    message: 'Nevernote',
                    description: 'Unhared'
                });
                window.location.reload();
            }).catch(error => {
                notification.error({
                    message: 'Nevernote',
                    description: error || 'Error unsharing. Please try again.'
                });
            })

            form.resetFields();
            this.setState({shareNoteModal: false});
        });
    }

    saveNoteFormRef = (noteFormRef) => {
        this.noteFormRef = noteFormRef;
    }

    shareNoteFormRef = (shareFormRef) => {
        this.shareFormRef = shareFormRef;
    }

    unshareNoteFormRef = (unShareFormRef) => {
        this.unShareFormRef = unShareFormRef;
    }

    saveNotebookFormRef = (notebookFormRef) => {
        this.notebookFormRef = notebookFormRef;
    }

    deleteNotebookFormRef = (deleteNotebookFormReference) => {
        this.deleteNotebookFormReference = deleteNotebookFormReference;
    }

    // Init state
    loadData() {
        this.setState({
            isLoading: true
        });

        getNotebooks().then(response => {
            this.setState({
                isLoading: false,
                bookList: response
            })
        }).catch(error => {
            this.setState({
                isLoading: false
            });
            notification.error({
                message: 'Nevernote',
                description: error || 'Error fetching latest notebooks. Please try again'
            });
        });
    }

    // Generate notebook combobox
    genNewNoteContents = data => data.map((item) => {
            return (
                <Option {...item} key={item.id} value={item.id}>{item.title}</Option>
            );
        }
    )

    // Move a note to the starred notebook
    starNote() {
        if (!this.isActiveNote()) {
            return;
        }

        this.saveNote();

        starNote(this.state.activeNote.id).then(response => {
            notification.info({
                message: 'Nevernote',
                description: 'Starred'
            });
            window.location.reload();
        }).catch(error => {
            notification.error({
                message: 'Nevernote',
                description: error || 'Error starring note. Please try again.'
            });
        })
    }

    // Is there a note in the editor at the moment?
    isActiveNote() {
        var activeNote = this.state.activeNote;
        return activeNote.id;
    }

    // Delete note confirmation box
    showConfirm(activeNote) {
        if (!this.isActiveNote()) {
            return;
        }

        var note = this.state.activeNote;

        confirm({
            title: 'Are you sure you want to delete this note?',
            content: 'This is not recoverable.',
            onOk() {
                deleteNote(note.id).then(response => {
                    notification.success({
                        message: 'Nevernote',
                        description: 'Note deleted'
                    });
                    window.location.reload()
                }).catch(error => {
                    notification.error({
                        message: 'Nevernote',
                        description: error || 'Error deleting. Please try again'
                    });
                });
            },
            onCancel() {
            },
        });
    }

    // Remove the currently loaded note
    closeEditor() {
        if (this.state.htmlLoaded) {
            this.saveNote();
            this.setState({
                htmlLoaded: false,
                activeNote: {}
            });
        }
    }

    render() {
        if (this.state.isLoading) {
            return <LoadingIndicator/>
        }
        return (
            <Layout style={{minHeight: '90vh', backgroundColor: '#FFFFFF'}}>
                <NewNoteForm
                    wrappedComponentRef={this.saveNoteFormRef}
                    visible={this.state.newNoteModal}
                    onCancel={this.handleCancelNote}
                    onCreate={this.handleCreateNote}
                    radioContents={this.genNewNoteContents(this.state.bookList)}
                />
                <NewNoteBookForm
                    wrappedComponentRef={this.saveNotebookFormRef}
                    visible={this.state.newNoteBookModal}
                    onCancel={this.handleCancelNotebook}
                    onCreate={this.handleCreateNotebook}/>
                <DeleteNotebookForm
                    wrappedComponentRef={this.deleteNotebookFormRef}
                    visible={this.state.deleteNoteBookModal}
                    onCancel={this.handleDeleteCancelNotebook}
                    onCreate={this.handleDeleteNotebook}
                    radioContents={this.genNewNoteContents(this.state.bookList)}/>
                <ShareNoteForm
                    wrappedComponentRef={this.shareNoteFormRef}
                    visible={this.state.shareNoteModal}
                    onCancel={this.handleCancelShare}
                    radioContents={this.genNewNoteContents(this.state.bookList)}
                    onCreate={this.handleShareNote}/>
                <UnShareNoteForm
                    wrappedComponentRef={this.unshareNoteFormRef}
                    visible={this.state.unShareNoteModal}
                    onCancel={this.handleCancelUnshare}
                    radioContents={this.genNewNoteContents(this.state.bookList)}
                    onCreate={this.handleUnshareNote}/>
                <Sider
                    collapsible
                    collapsed={this.state.collapsed}
                    onCollapse={this.onCollapse}>
                    <Menu theme="dark" selectable={false} mode="inline">
                        <Menu.Item key="1" onClick={this.showNewNoteBookModal.bind(this)}>
                            <Icon type="plus-square"/>
                            <span>New Notebook</span>
                        </Menu.Item>
                        <Menu.Item key="2" onClick={this.showDeleteNotebookModal.bind(this)}>
                            <Icon type="delete"/>
                            <span>Delete Notebook</span>
                        </Menu.Item>
                        <Menu.Item key="3" onClick={this.showNewNoteModal.bind(this)}>
                            <Icon type="plus"/>
                            <span>New Note</span>
                        </Menu.Item>
                        <Menu.Item key="4" onClick={this.saveNote.bind(this)}>
                            <Icon type="save"/>
                            <span>Save Note</span>
                        </Menu.Item>
                        <Menu.Item key="5" onClick={this.showConfirm.bind(this)}>
                            <Icon type="delete"/>
                            <span>Delete Note</span>
                        </Menu.Item>
                        <Menu.Item key="6" onClick={this.starNote.bind(this)}>
                            <Icon type="star"/>
                            <span>Star Note</span>
                        </Menu.Item>
                        <Menu.Item key="7" onClick={this.showShareNoteModal.bind(this)}>
                            <Icon type="share-alt"/>
                            <span>Share Notebook</span>
                        </Menu.Item>
                        <Menu.Item key="8" onClick={this.showUnshareNoteModal.bind(this)}>
                            <Icon type="share-alt"/>
                            <span>Unshare Notebook</span>
                        </Menu.Item>
                    </Menu>
                </Sider>
                <Content style={{margin: '0 16px'}}>
                    <Breadcrumb style={{margin: '16px 0'}}>
                        <Breadcrumb.Item>{this.state.activeNote.notebookName}</Breadcrumb.Item>
                        <Breadcrumb.Item>{this.state.activeNote.title}</Breadcrumb.Item>
                        {this.state.readOnly &&
                        <Breadcrumb.Item style={{textAlign: 'center', fontSize: 12, fontWeight: 'bold'}}>Read
                            Only</Breadcrumb.Item>}
                    </Breadcrumb>
                    <div>
                        {this.state.htmlLoaded && <Editor readOnly={this.state.readOnly} html={this.state.editorHtml}
                                                          onEvent={this.saveEditorHtml.bind(this)}/>}
                        {!this.state.htmlLoaded &&
                        <Text style={{textAlign: 'center', fontSize: 18}}>Choose a note to load using the tree on the
                            right</Text>}
                    </div>

                </Content>
                <NoteList onEvent={this.updateEditor.bind(this)} onClose={this.closeEditor.bind(this)}/>
            </Layout>
        );
    }
}

export default SiderComponent;