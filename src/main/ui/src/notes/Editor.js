import React from 'react';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.snow.css';
import './Editor.css'

// RTF editor configuration
class Editor extends React.Component {
    modules = {
        toolbar: [
            [{'header': '1'}, {'header': '2'}, {'font': []}],
            [{size: []}],
            ['bold', 'italic', 'underline', 'strike', 'blockquote'],
            [{'list': 'ordered'}, {'list': 'bullet'},
                {'indent': '-1'}, {'indent': '+1'}],
            // Links only, no images or videos
            ['link'],
            ['clean']
        ],
        clipboard: {
            matchVisual: false,
        }
    }

    formats = [
        'header', 'font', 'size',
        'bold', 'italic', 'underline', 'strike', 'blockquote',
        'list', 'bullet', 'indent',
        'link'
    ]

    constructor(props) {
        super(props)
        this.handleChange = this.handleChange.bind(this)
    }

    // Fire the parent's callback
    handleChange(html) {
        this.props.onEvent(html);
    }

    render() {
        return (
            <div>
                <ReactQuill
                    theme={"snow"}
                    onChange={this.handleChange}
                    modules={this.modules}
                    formats={this.formats}
                    bounds={'.app'}
                    readOnly={this.props.readOnly}
                    style={{height: "75vh", width: "70vw"}}
                    value={this.props.html}
                    placeholder={"Take a note..."}
                />
            </div>
        )
    }
}

export default Editor;