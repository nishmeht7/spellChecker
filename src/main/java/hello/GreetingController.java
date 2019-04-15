package hello;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.*;

@RestController
public class GreetingController {

    private Dictionary dict = new CompactPrefixTree("input/words_ospd.txt");

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(required=false, defaultValue="World") String name) {
        System.out.println("==== in greeting ====");

        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/spell")
    public void getWords() {
        System.out.println("==== in getWords ====");

    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/word")
    public String[] getSuggestion(@RequestBody String word) {
        System.out.println(" === in getSuggestion ===");

        word = word.replaceAll("\"", "").toLowerCase();
        System.out.println("The word is: " + word);

        String[] suggestions = dict.suggest(word, 4);

        for (String suggestion : suggestions) {
            System.out.println("suggestion: " + suggestion);
        }

        return suggestions;

    }

}

