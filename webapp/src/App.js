import React, { Component } from 'react';
import './App.css';

class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      suggestions: [""],
      value: ""
    }
  }

  onChangeHandler = (e) => {
    let word = e.target.value;
    this.setState({value: word});
    this.postData("http://localhost:8080/word", word)
    .then(data => {
      console.log(JSON.stringify(data))
      this.setState({suggestions: [...data]})
    })
    .catch(error => console.error(error));
  }


  postData = (url = "", data = {}) => {
    // Default options are marked with *
      return fetch(url, {
          method: "POST", // *GET, POST, PUT, DELETE, etc.
          mode: "cors", // no-cors, cors, *same-origin
          cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
          credentials: "same-origin", // include, *same-origin, omit
          headers: {
              "Content-Type": "application/json",
              // "Content-Type": "application/x-www-form-urlencoded",
          },
          redirect: "follow", // manual, *follow, error
          referrer: "no-referrer", // no-referrer, *client
          body: JSON.stringify(data), // body data type must match "Content-Type" header
      })
      .then(response => response.json()); // parses JSON response into native Javascript objects
  }

  onSuggestionClick = word => {
      this.setState({value: word});
  }

  render() {

    const { suggestions, value } = this.state;

    return (
      <div className="bodyWrapper">
        <form>
          <input className="input" type="text" placeholder="spell checker" onChange={this.onChangeHandler} value={value} />
          <span className="suggestionsWrapper">
            {suggestions.map(suggestion => {
              return suggestion != null ?
              <p onClick={() => this.onSuggestionClick(suggestion)} className="suggestions" key={suggestion}>
                {suggestion}
              </p>
              :
              null;
            }
            )}
          </span>
        </form>
      </div>
    );
  }
}

export default App;
