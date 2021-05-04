import ChatBox from "./components/ChatBox";
import { BrowserRouter as Router, Route, Redirect } from 'react-router-dom'

function App() {
  return (
    <div className="App" style={{ height: '100vh', display: "flex", alignItems: "center", justifyContent: "center" }}>
      <Router>
        <Route exact path='/' render={() => <div></div>} />
        <Route exact path='/chat' render={() => <ChatBox />} />
      </Router>
    </div>
  );
}

export default App;
