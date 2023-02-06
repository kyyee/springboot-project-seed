'use strict';

import { Snowflake } from './snowflake.js'

class FileSlice {
    // 每个chunk的大小，设置为10兆
    static chunkSize = 10 * 1024 * 1024;
    constructor(configs) {
        this.configs = {
        };
        if (configs) {
            Object.assign(this.configs, configs);
        }
    };

    init() {
        // 检测 DOMContentLoaded 是否已完成
        document.readyState !== 'loading' ? this._doStart(this.configs) : document.addEventListener('DOMContentLoaded', () => this._doStart(this.configs));
    }

    _doStart(configs) {
        // 获取slice方法，做兼容处理
        // 提交
        const sliceUploadSubmitBtn = document.getElementById('sliceUploadSubmitBtn');
        sliceUploadSubmitBtn.addEventListener('click', async() => {
            let index = 0
            // 1.读取文件
            let fileDom = document.getElementById("sliceUploadFile");
            let files = fileDom.files;
            let file = files[0];
            if (!file) {
                alert('请选择文件');
                return;
            }
            // 2.设置分片参数属性、获取文件MD5值
            let chunkSize = FileSlice.chunkSize;
            sliceUploadSubmitBtn.disabled = true;
            fileDom.disabled = true;
            const md5 = await this._getFileMd5(file, chunkSize).then(md5 => {
                console.log(md5);
                sliceUploadSubmitBtn.disabled = false;
                fileDom.disabled = false;
                let id = Snowflake.getId();
                this._sliceUploadFile(file, index, chunkSize, id, md5);
            })
            // todo 暂停，断点续传
        });

        const uploadSubmitBtn = document.getElementById('uploadSubmitBtn');
        uploadSubmitBtn.addEventListener('click', async() => {
            let index = 0
            // 1.读取文件
            let fileDom = document.getElementById("uploadFile");
            let files = fileDom.files;
            let file = files[0];
            if (!file) {
                alert('请选择文件');
                return;
            }
            // 2.设置分片参数属性、获取文件MD5值
            let chunkSize = FileSlice.chunkSize;
            uploadSubmitBtn.disabled = true;
            fileDom.disabled = true;
            const md5 = await this._getFileMd5(file, chunkSize).then(md5 => {
                console.log(md5);
                uploadSubmitBtn.disabled = false;
                fileDom.disabled = false;
                let id = Snowflake.getId();
                this._uploadFile(file, id, md5);
            })
        });
    }

    // 对文件进行MD5加密(文件内容+文件标题形式)
    _getFileMd5(file, chunkSize) {
        return new Promise((resolve, reject) => {
            let blobSlice = File.prototype.slice || File.prototype.mozSlice || File.prototype.webkitSlice;
            let index = 0;
            let chunks = Math.ceil(file.size / chunkSize);
            let spark = new SparkMD5.ArrayBuffer();
            let fileReader = new FileReader();

            fileReader.onload = (e) => {
                spark.append(e.target.result); // Append array buffer
                index++;
                if (index < chunks) {
                    let start = index * chunkSize;
                    let end = Math.min(file.size, start + chunkSize);
                    fileReader.readAsArrayBuffer(blobSlice.call(file, start, end));
                } else {
                    let result = spark.end();
                    resolve(result);
                    // 通过内容和文件名称进行md5加密
                    //          let sparkMd5 = new SparkMD5();
                    //          sparkMd5.append(result);
                    //          sparkMd5.append(file.name);
                    //          const hexHash = sparkMd5.end();
                    //          resolve(hexHash);
                }
            };
            fileReader.onerror = (e) => {
                reject(e);
                alert('文件读取失败！');
            };
            // 文件分片
            let start = index * chunkSize;
            let end = Math.min(file.size, start + chunkSize);
            fileReader.readAsArrayBuffer(blobSlice.call(file, start, end));
        })
        .catch(err => { console.error(err); });
    }

    // 文件上传
    _sliceUploadFile(file, index, chunkSize, id, md5) {
        if (file) {
            let blobSlice = File.prototype.slice || File.prototype.mozSlice || File.prototype.webkitSlice;

            let chunks = Math.ceil(file.size / chunkSize); // 分片总数
            if (index > chunks - 1) {
                return;
            }
            let start = index * chunkSize;
            const end = Math.min(file.size, start + chunkSize);
            // 构建表单
            let form = new FormData();
            // blobSlice.call(file, start, end)方法是用于进行文件分片
            form.append('file', blobSlice.call(file, start, end));
            form.append('md5', md5);
            form.append('chunk', index);
            form.append('chunks', chunks);
            form.append('filename', file.name);
            form.append('size', chunkSize);
            form.append('id', id);
            // ajax提交 分片，此时 content-type 为 multipart/form-data
            fetch('/api/kyyee/v2/sps/files/slice-upload', {
                method: 'post',
//              // 不能设置 headers :{ 'Content-Type': 'multipart/form-data', }
                body: form,
                })
            .then(response => response.json())
            .catch(error => { console.error(error) })
            .then(async success => {
                console.debug(success);
                let isSuccess = success.code === '0000000000' && success.data;
                if (isSuccess) {
                    // 判断分片是否上传完成
                    let uri = success.data.uri;
                    if (uri) {
                        // 4.所有分片上传后，请求合并分片文件
                        this._setPercent(chunks, chunks); // 全部上传完成
                        let fileInfo = {
                            name: file.name,
                            size: file.size,
                            type: file.type,
                            uid: file.uid,
                            status: 'done',
                            uri: uri,
                            server_ip: success.data.server_ip,
                        };
                        const sliceUploadFileHref = document.getElementById('sliceUploadFileHref');
                        sliceUploadFileHref.href = window.location.protocol + '//' + window.location.host + uri;
                        sliceUploadFileHref.textContent = fileInfo.name;
                        console.log(fileInfo);
                    } else {
                        this._setPercent(index, chunks);
                        await this._sliceUploadFile(file, ++index, chunkSize, id, md5);
                    }
                } else {
                    alert("上传失败");
                    return;
                }
            });
        }
    }

    // 文件上传
    _uploadFile(file, id, md5) {
        if (file) {
            let blobSlice = File.prototype.slice || File.prototype.mozSlice || File.prototype.webkitSlice;

            // 构建表单
            let form = new FormData();
            // blobSlice.call(file, start, end)方法是用于进行文件分片
            form.append('file', file);
            form.append('md5', md5);
            form.append('filename', file.name);
            form.append('size', file.size);
            form.append('id', id);
            // ajax提交 分片，此时 content-type 为 multipart/form-data
            fetch('/api/kyyee/v2/sps/files/upload', {
                method: 'post',
//              // 不能设置 headers :{ 'Content-Type': 'multipart/form-data', }
                body: form,
                })
            .then(response => response.json())
            .catch(error => { console.error(error) })
            .then(async success => {
                console.debug(success);
                let isSuccess = success.code === '0000000000' && success.data;
                if (isSuccess) {
                    // 判断分片是否上传完成
                    let uri = success.data.uri;
                    if (uri) {
                        let fileInfo = {
                            name: file.name,
                            size: file.size,
                            type: file.type,
                            uid: file.uid,
                            status: 'done',
                            uri: uri,
                            server_ip: success.data.server_ip,
                        };
                        const uploadFileHref = document.getElementById('uploadFileHref');
                        uploadFileHref.href = window.location.protocol + '//' + window.location.host + uri;
                        uploadFileHref.textContent = fileInfo.name;
                        console.log(fileInfo);
                    }
                } else {
                    alert("上传失败");
                    return;
                }
            });
        }
    }

    // 设置进度条
    _setPercent(index, chunks) {
        let percentValue = Math.ceil(index / chunks * 100);
        // 进度条
        const percentDom = document.getElementById('percentRange');
        percentDom.value = percentValue;
        percentDom.style.background = `-webkit-linear-gradient(top, #059CFA, #059CFA) 0% 0% / ${percentValue}% 100% no-repeat`;
        // 进度条值对应dom
        const percentSpan = document.getElementById('percentSpan');
        percentSpan.textContent = percentValue + '%';
     }
}

new FileSlice({}).init();
