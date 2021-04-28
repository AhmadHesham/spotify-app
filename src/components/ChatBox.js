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
                        token: 'eyJhbGciOiJIUzI1NiJ9.eyJ0b2tlbl91c2VyX2lkIjoxLCJsYXN0X25hbWUiOiJyYW1hZGFuIiwiZXhwaXJhdGlvbiI6MTAwMDAwMDAwMDAwMDAwMCwiZmlyc3RfbmFtZSI6InlvdXNlZiIsImVtYWlsIjoieW91c2VmQGdtYWlsLmNvbSIsInVzZXJuYW1lIjoieW91c2VmIn0.D41pTHsd6cllp1uzeGgVND2vuVF03wKjEeTtaT6ULbg'
                    }
                }
            )
                .then(response => {
                    console.log(`ChatID: ${response.data.data}`);
                    setWs(new WebSocket(`ws://127.0.0.1:9090/chat?chatID=${response.data.data}`));
                    setConnected(true);
                })
                .catch(err => {
                    console.log(err);
                })
        }
    }

    const handleConnect = async () => {
        setWs(new WebSocket(`ws://127.0.0.1:9090/chat?chatID=${text}`));
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
                    token: 'eyJhbGciOiJIUzI1NiJ9.eyJ0b2tlbl91c2VyX2lkIjoxLCJsYXN0X25hbWUiOiJyYW1hZGFuIiwiZXhwaXJhdGlvbiI6MTAwMDAwMDAwMDAwMDAwMCwiZmlyc3RfbmFtZSI6InlvdXNlZiIsImVtYWlsIjoieW91c2VmQGdtYWlsLmNvbSIsInVzZXJuYW1lIjoieW91c2VmIn0.D41pTHsd6cllp1uzeGgVND2vuVF03wKjEeTtaT6ULbg'
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
                // console.log(msg)
                let newMessages = [...messages];
                newMessages.push({
                    "text": msg.data,
                    "timeStamp": Date.parse(new Date())
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
                                        <Typography style={{display: 'inline-block', marginRight: '0.5vw', fontWeight: 'bold'}}>Anon: </Typography>
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
