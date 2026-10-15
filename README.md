Reflection:



• What changed from your specification? Classes, method signatures, where validation lives, test cases — what did the spec say, what did you build instead, and why?



• What changed in the LinkedChain? Anything you added to or modified in the provided class and why your application needed it, or that you used it unchanged.



First some of the naming conventions were changed to fit the naming in the spec doc in order to minimize conflict with other team members. Then reference to the last node aka a tail was added in order to make some operations more efficient. Also, a method to shuffle a node from the front to the back was added. Finally, operations were added to get the front and back of the chain to ease testing and add functionality. The rest of the linked chain class was left unchanged as it works well enough for our purposes.



• What happened to your unknowns? Of the things you listed in your scope as not knowing how to do, which did you figure out, and which are still open?

From our unknowns handling edge cases such as a chain with 1 or 2 unknowns was figured out as it now should be handled automatically in the chain. However, for the other unknowns we still do not know of what GUI packages we can utilize and how will they work with our other packages. Also, we still do not know how to safely handle the game state when out of questions with power-ups active and/or in the inventory.



• What doesn't work yet? MVP features, bad-input cases, or tests that are missing or failing in this prototype.

The linked chain implementation has some errors when adding multiple cards. Also, for the linked chain for the most part only the tests from the spec were added so there is more possible test cases to add. Finally, there isn’t many questions yet for the cards to have so more should be added.



Next steps:



• What is left to finish the MVP? The remaining features and fixes, in the order you will do them.



• Which stretch goals will you attempt? The ones from your scope you are committing to, and the ones you are dropping.



• What new tests will you need? The methods and bad-input cases the added work must cover.



• Who does what? Which team member owns each remaining item.



Attribution:

| File | Who | How |

| --- | --- | --- |

| Card | Julia | Wrote |

| CardTest | Julia | Wrote |

| CardType | Julia | Wrote |

| FRQCard | Julia | Wrote |

| FRQTest | Julia | Wrote |

| LinkedChain | Caleb Terstegen | Revised |

| LinkedChainTest | Caleb Terstegen | Wrote |

| MultipleChoiceCard | Julia | Wrote |

| MultipleChoiceCardTest | Julia | Wrote |

| PowerUpCard | Julia | Wrote |

| PowerUpCardType | Julia | Wrote |

| QuestionCard | Julia | Wrote |

| TrueFalseCard | Julia | Wrote |

| TrueFalseCardTest | Julia | Wrote |

