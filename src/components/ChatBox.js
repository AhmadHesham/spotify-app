import React from 'react'
import { makeStyles } from '@material-ui/core/styles';
import Card from '@material-ui/core/Card';
import CardActions from '@material-ui/core/CardActions';
import CardContent from '@material-ui/core/CardContent';
import Button from '@material-ui/core/Button';
import Typography from '@material-ui/core/Typography';
import { TextField } from '@material-ui/core';
import axios from 'axios';

const useStyles = makeStyles({
    card: {
        height: "100%",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        flexDirection: "column",
        width: '100%'
    },
    title: {
        fontSize: 14,
    },
    pos: {
        marginBottom: 12,
    },
});

export default function ChatBox() {
    const [connected, setConnected] = React.useState(false);
    const [text, setText] = React.useState('');
    const [chatID, setChatID] = React.useState('');
    const [ws, setWs] = React.useState();
    const classes = useStyles();

    const [messages, setMessages] = React.useState([]);

    const handleChange = (e) => {
        setText(e.target.value);
    }

    const handleSend = () => {
        if (connected) {
            ws.send(text);
        }
    }

    const handleCreate = async () => {
        if (!connected) {
            await axios.post(
                'http://127.0.0.1:8080/testing-rabbit/create-chat',
                {
                    "queue": "testing-rabbit",
                    "method": "create-chat",
                    "participants": "1;5;20"
                },
                {
                    headers: {
                        token: 'eyJhbGciOiJIUzI1NiJ9.eyJ0b2tlbl9lbWFpbCI6Im1hbmdhM0Bob3RtYWlsLmNvbSIsInRva2VuX3VzZXJfaWQiOiI5IiwidG9rZW5fbmFtZSI6Im1hbmdhIiwidG9rZW5fYml0X3JhdGUiOiIxMjgiLCJleHBpcmF0aW9uIjoxNjE5OTM5MTM3NzQwLCJ0b2tlbl91c2VybmFtZSI6Im1hbmdhMyIsInRva2VuX3R5cGUiOiJ1c2VyIiwidG9rZW5faXNfcHJlbWl1bSI6ImYifQ.e5bQ__mBTOnJI5IGBfGHWdZNkxEh_KzO9X7ahZJ38q0'
                    }
                }
            )
                .then(response => {
                    console.log(`ChatID: ${response.data.data}`);
                    setWs(new WebSocket(`ws://127.0.0.1:9090/chat?chatID=${response.data.data}?token=eyJhbGciOiJIUzI1NiJ9.eyJ0b2tlbl9lbWFpbCI6Im1hbmdhM0Bob3RtYWlsLmNvbSIsInRva2VuX3VzZXJfaWQiOiI5IiwidG9rZW5fbmFtZSI6Im1hbmdhIiwidG9rZW5fYml0X3JhdGUiOiIxMjgiLCJleHBpcmF0aW9uIjoxNjE5OTM5MTM3NzQwLCJ0b2tlbl91c2VybmFtZSI6Im1hbmdhMyIsInRva2VuX3R5cGUiOiJ1c2VyIiwidG9rZW5faXNfcHJlbWl1bSI6ImYifQ.e5bQ__mBTOnJI5IGBfGHWdZNkxEh_KzO9X7ahZJ38q0`));
                    setConnected(true);
                })
                .catch(err => {
                    console.log(err);
                })
        }
    }

    const handleConnect = async () => {
        setWs(new WebSocket(`ws://127.0.0.1:9090/chat?chatID=${text}?token=eyJhbGciOiJIUzI1NiJ9.eyJ0b2tlbl9lbWFpbCI6Im1hbmdhM0Bob3RtYWlsLmNvbSIsInRva2VuX3VzZXJfaWQiOiI5IiwidG9rZW5fbmFtZSI6Im1hbmdhIiwidG9rZW5fYml0X3JhdGUiOiIxMjgiLCJleHBpcmF0aW9uIjoxNjE5OTM5MTM3NzQwLCJ0b2tlbl91c2VybmFtZSI6Im1hbmdhMyIsInRva2VuX3R5cGUiOiJ1c2VyIiwidG9rZW5faXNfcHJlbWl1bSI6ImYifQ.e5bQ__mBTOnJI5IGBfGHWdZNkxEh_KzO9X7ahZJ38q0`));
        setConnected(true);
        const data = {
            queue: 'testing-rabbit',
            method: 'get-chat-logs',
            chatID: text
        }
        await axios.post(
            'http://127.0.0.1:8080/testing-rabbit/get-chat-logs',
            data,
            {
                headers: {
                    token: 'eyJhbGciOiJIUzI1NiJ9.eyJ0b2tlbl9lbWFpbCI6Im1hbmdhM0Bob3RtYWlsLmNvbSIsInRva2VuX3VzZXJfaWQiOiI5IiwidG9rZW5fbmFtZSI6Im1hbmdhIiwidG9rZW5fYml0X3JhdGUiOiIxMjgiLCJleHBpcmF0aW9uIjoxNjE5OTM5MTM3NzQwLCJ0b2tlbl91c2VybmFtZSI6Im1hbmdhMyIsInRva2VuX3R5cGUiOiJ1c2VyIiwidG9rZW5faXNfcHJlbWl1bSI6ImYifQ.e5bQ__mBTOnJI5IGBfGHWdZNkxEh_KzO9X7ahZJ38q0'
                }
            }
        )
        .then(res => {
            // setMessages
            // console.log(JSON.parse(res.data.data));
            // console.log(Date.parse(JSON.parse(res.data.data)[0]["sentDate"]));
            let logs = JSON.parse(res.data.data)
            logs.map((log) => {
                log["timeStamp"] = Date.parse(log["sentDate"]);
            })
            console.log(logs);
            setMessages(logs);
            
        })
        .catch(err => {
            console.log(err)
        })
    }

    const messagesRef = React.useRef();

    React.useEffect(() => {
        console.log('lmao');
        if (connected) {
            messagesRef.current.scrollIntoView();
            ws.onmessage = (msg) => {
                console.log('message hehe');
                console.log(msg)
                let newMessages = [...messages];
                msg = JSON.parse(msg.data);
                newMessages.push({
                    senderName: msg.senderName,
                    text : msg.text,
                    timeStamp: Date.parse(new Date())
                })
                newMessages.sort((a, b) => {
                    if (a.timeStamp < b.timeStamp) {
                        return -1
                    }
                    if (a.timeStamp > b.timeStamp) {
                        return 1
                    }
                    return 0;
                })
                setMessages(newMessages);
            }
        }
    }, [connected, messages])

    return (
        <div style={{ height: "70%", width: "50%", display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
            <Card className={classes.card}>
                <CardContent style={{ height: "75%", width: '100%', justifyContent: 'center', alignItems: 'center', display: 'flex', flexDirection: 'column' }}>
                    <Typography variant="h6" style={{ marginBottom: '0.5vw' }}>
                        Spotify Chat
                    </Typography>
                    <div style={{ height: '90%', width: '80%', border: '3px solid rgba(127, 127, 127, 0.5)', borderRadius: 15, overflowY: 'auto' }}>
                        {
                            messages.map((msg, key) => {
                                return (
                                    <div key={key} style={{marginTop: '1vw', marginLeft: '1vw', marginBottom: '1vw'}}>
                                        <Typography style={{display: 'inline-block', marginRight: '0.5vw', fontWeight: 'bold'}}>{msg.senderName}: </Typography>
                                        <Typography style={{display: 'inline-block' }} variant="body1">
                                            {msg.text}
                                        </Typography>
                                    </div>
                                )
                            })
                        }
                    <div ref={messagesRef} />
                    </div>
                </CardContent>
                <CardActions style={{ width: "100%", display: "flex", flexDirection: "column"}}>
                    <TextField onChange={handleChange} id="outlined-basic" label="Enter your message" variant="outlined" style={{ width: "80%", marginBottom: "0.5vw" }} />
                    <div style={{ height:'40%', display: "flex", justifyContent: "center", width: "100%", flexDirection: "row"}}>
                        <Button color="primary" onClick={handleSend} style={{ marginRight: "1vw" }} disabled={!connected}>Send</Button>
                        <Button color="primary" onClick={handleCreate} disabled={connected} style={{ marginRight: "1vw" }}>Create</Button>
                        <Button color="primary" onClick={handleConnect} disabled={connected}>Connect</Button>
                    </div>
                </CardActions>
            </Card>
        </div>
    )
}
