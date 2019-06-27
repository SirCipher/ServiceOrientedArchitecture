const path = require('path');
const merge = require('webpack-merge');
const TARGET = process.env.npm_lifecycle_event;
const PATHS = {
    source: path.join(__dirname, 'app'),
    output: path.join(__dirname, '../../../target/classes/static')
};

const fs  = require('fs');
const lessToJs = require('less-vars-to-js');
const themeVariables = lessToJs(fs.readFileSync(path.join(__dirname, './ant-theme-vars.less'), 'utf8'));

const common = {
    entry: "./src/index.js",
    output: {
        path: PATHS.output,
        publicPath: '',
        filename: 'bundle.js'
    },
    module: {
        rules: [{
            exclude: /node_modules/,
            use: [{
                loader: 'babel-loader',
                options: {
                    // presets: ['react'],
                    plugins: [
                        'transform-class-properties', 
                        'transform-object-rest-spread', 
                        ['import', { libraryName: "antd", style: true }],
                        ["module-resolver", {
                            "alias": {
                              "^react-native$": "react-native-web"
                            }
                        }]
                    ]
                }
            }]
        }, {
            test: /\.css$/,
            use: [ 'style-loader', 'css-loader' ]
        },{
            test: /\.less$/,
            use: [
              {loader: "style-loader"},
              {loader: "css-loader"},
              {loader: "less-loader",
                options: {
                  modifyVars: themeVariables,
                  javascriptEnabled: true
                }
              }
            ]
          }
        ]
    },
    resolve: {
        extensions: ['*', '.js', '.jsx']
    }
};

if (TARGET === 'start' || !TARGET) {
    module.exports = merge(common, {
        devServer: {
            port: 9090,
            proxy: {
                '/': {
                    target: 'http://localhost:8080',
                    secure: false,
                    prependPath: false
                }
            },
            publicPath: 'http://localhost:9090/',
            historyApiFallback: true
        },
        devtool: 'source-map'
    });
}

if (TARGET === 'build') {
    module.exports = merge(common, {});
}

module.exports.mode = 'development';