# Contributing Guides

## Intro

This is the contributing guidelines for Ornithe Carpet 1.12.


## General

### Naming Conventions

Refer to [Yarn](https://github.com/FabricMC/yarn/blob/1.21.1/CONVENTIONS.md) and [Feather](https://github.com/OrnitheMC/feather-mappings/blob/main/CONVENTIONS.md) mappings' naming conventions

In simple terms: 
* Use `UpperCamelCase` for class names
* Use `lowerCamelCase` for rule names, method names, fields and variable names
* If acronyms are needed, make sure they are somewhat readable, treat acronyms as single words is preferred

### Code Style

If you are not sure what you are doing, apply [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
and use IDEA's default code formater.

## Rule

Rules are annotated by `@Rule`, they should include at least rule description, categories and recommend options.

Boolean rules don't require options, since they will automatically be added `true, false` options.

TODO

### Rule Category

When categorizing rules, put rules under categories based on the feature of the rule, then the overall categories.

For command rules that associated with other rules, they should follow the categories of original rule.

For example, `explosionNoBlockDamage` should be put under `TNT` category first, then `CREATIVE`.



### Validators

Some rules might require a validator class to validate the rule value,


## Mixin Class

Mixin classes are located under `mixins` package

For general carpet rule mixins, place them under `mixins.rule.ruleName`

For client side mixins, place them under `mixins.rule.ruleName.client`

For mixin classes, name as `OriginalClassMixin`, e.g. `Entity` -> `EntityMixin`

For mixin method names, make sure method names have actual meanings indicating what method do or try to describe the purpose.
For example, if you mixin into a `tick()` method to count how many times it got ticked, name it `countTickTimes`,
do not name it `onTick` or whatever

For mixin annotations, see Example mixin method.

Example mixin path tree:
```
├─mixins
│  └─rule
│      └─someRuleName
│          │─client
│          │   └─SomeClientMixin.java
│          └─SomeCommonMixin.java
```

Example mixin method:
```java
@Mixin(SomeClass.class)
public abstract class SomeClassMixin {
    // simple mixin annotation
    @Inject(method = "tick", at = @At("HEAD"))
    private void doSomething(CallbackInfo ci) {
        // code here
    }
    
    // mixin annotations that have target
    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "some/mixin/target;method()V"
            )
    )
    private void doSomeOtherThing(CallbackInfo ci) {
        // code here
    }
}
```

### Accessor and Fake Interfaces

Accessors can be shared with different classes, they should be put under `mixins.accessor`.

Fake interfaces might also be shared, put them under `carpet.fakes`.
Name fake interfaces as `SomeClassInterface`, the implementation of the method in mixin class should be put under rule's mixin package.

## Project Structure

TODO
