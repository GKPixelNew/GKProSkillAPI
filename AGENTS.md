AGENTS — Adding a component to the editor (generic)

Purpose
-------
This document captures a generic, repeatable process for adding new editor components to the GKProSkillAPIEditor UI. It is intended for contributors and automated agents so new components (mechanics, triggers, targets, conditions) can be added consistently and without introducing type/registry errors.

Scope
-----
Applies to UI editor components implemented in:
- `src/api/components/components.svelte.ts` — primary file that contains class definitions for triggers, targets, conditions, and mechanics and the registry mappings used by the editor UI.
- Option component imports at the top of the same file (e.g., `StringSelect`, `DoubleSelect`, `AttributeSelect`, etc.).

Quick checklist
---------------
- [ ] Decide component type: Mechanic, Trigger, Target, or Condition.
- [ ] Add/verify imports for any Option components used (StringSelect, DoubleSelect, AttributeSelect, DropdownSelect, IntSelect, BooleanSelect, etc.).
- [ ] Add new class extending the appropriate base (FabledMechanic, FabledTrigger, FabledTarget, or FabledCondition).
- [ ] Place the class definition before the registry that references it (avoid forward-reference errors).
- [ ] Update the corresponding registry mapping (mechanics.set, triggers.set, targets.set, or conditions.set) with a key, display name, and component reference.
- [ ] Run TypeScript compile/error checks for `src/api/components/components.svelte.ts`.
- [ ] Commit with a focused message and optionally update this document.

Step-by-step guide
------------------
1) Decide component type
   - Mechanic: extends `FabledMechanic`
   - Trigger: extends `FabledTrigger`
   - Target: extends `FabledTarget`
   - Condition: extends `FabledCondition`

2) Prepare imports
   - At the top of `components.svelte.ts` you will see imports for common Option classes (StringSelect, AttributeSelect, DoubleSelect, DropdownSelect, IntSelect, BooleanSelect, SectionMarker, etc.).
   - If you use a new option type, add its import at the top with the other options.

3) Add the class
   - Locate an appropriate insertion point among the other classes (grouped by type — triggers, targets, conditions, mechanics). Typically class definitions appear before the `*.set` registry calls.
   - Add a small, self-contained class. Keep the constructor consistent with other classes: call `super({ name, description, data, preview?, summaryItems? }, <hasChildrenBoolean>);`
   - Keep names and keys consistent and human-friendly.

Generic class template (mechanic)
---------------------------------
Use this snippet as a starting template; adapt field names, types and defaults to your component's needs.

```ts
class ExampleMechanic extends FabledMechanic {
  public constructor() {
    super({
      name: 'Example',
      description: 'Short description of what this mechanic does.',
      data: [
        new StringSelect('Key', 'key', 'default').setTooltip('Tooltip for key'),
        new AttributeSelect('Amount', 'amount', 1).setTooltip('Amount to use'),
        new DoubleSelect('Speed', 'speed', 0.1).setTooltip('Playback speed')
      ],
      summaryItems: ['key', 'amount']
    }, false);
  }

  public static override new = () => new this();
}
```

Trigger / Target / Condition template
------------------------------------
Same pattern applies; change the base class (FabledTrigger, FabledTarget, FabledCondition) and populate `data`, `preview` and `summaryItems` as needed.

4) Placement rules
   - The class must be declared before the registry that references it. The registries are the calls near the bottom of the file: `triggers.set({...})`, `targets.set({...})`, `conditions.set({...})`, and `mechanics.set({...})`.
   - Prefer inserting new classes near other related classes for discoverability (e.g., animation-related mechanics nearby existing Animation/Particle mechanics).

5) Registry mapping
   - Find the appropriate registry call and add an entry with a unique key (conventionally ALL_CAPS_SNAKE) and an object { name: '<Human Name>', component: <ClassName>, section?: '<SectionName>' }.
   - Example:

```ts
// under mechanics.set({ ... })
EXAMPLE: { name: 'Example', component: ExampleMechanic, section: 'Misc' }
```

   - Keys are only used in the UI registry; ensure any runtime mechanic key (from Java/Kotlin) is matched in the UI mapping so users can find the right editor component.

6) Type-check and validate
   - After editing, run the project's TypeScript/compile error checking for the modified file. If you see a TS2304 error such as "Cannot find name 'X'", it usually means the registry references the class before it is declared. Move the class above the registry and re-check.
   - The workspace's error-checking tool should be used to verify there are no missing imports or type errors.

7) Commit and document
   - Make a focused commit with a clear message describing the new UI component and any notable details (default values, special requirements).
   - Optionally add a short note to this document describing the new component and why it was placed where it was.

Common pitfalls & troubleshooting
--------------------------------
- TS2304: Cannot find name 'MyComponent'
  - Cause: The registry references the class before it is declared.
  - Fix: Move the class declaration above the relevant registry (`mechanics.set`, `triggers.set`, `targets.set`, or `conditions.set`).

- Missing import for an Option component
  - Cause: Using an option type (e.g., ColorSelect) without importing it at the top of the file.
  - Fix: Add the import next to other option imports. Follow the file's existing import style.

- Registry key confusion / mismatch with runtime
  - Cause: The editor registry key differs from the runtime mechanic's key in Java/Kotlin.
  - Fix: Keep the UI registry key consistent with the runtime key. If the runtime uses a different canonical key, either add the mapping or update this document to note the mapping.

- Large file sensitivity
  - `components.svelte.ts` is large. Make minimal edits and avoid reformatting unrelated areas to reduce merge conflicts.

Example: adding a new animation mechanic (summary)
-------------------------------------------------
- Choose base: `FabledMechanic`.
- Add class `MyAnimationMechanic` in the mechanics section before `mechanics.set`.
- Add registry entry under `mechanics.set`: MY_ANIMATION: { name: 'My Animation', component: MyAnimationMechanic, section: 'Animation' }
- Run typecheck; if errors mention missing symbol, move the class earlier.

Verification & quality gates
----------------------------
Before marking the change done, at minimum run these checks:
- [x] TypeScript compile / error check on `src/api/components/components.svelte.ts` (no errors).
- [x] Quick manual sanity: open the editor UI and confirm the new component appears (optional/manual).

Commit message suggestions
--------------------------
- editor: add <ComponentName> component (list of option keys)
- editor: add <ComponentName> and registry mapping

If you want me to:
- Add a small preview section template for mechanics that support visual previews, I can add a recommended `preview` array template.
- Insert a concrete example class directly into the file in a separate PR/commit, or move the existing BetterModel example to an example folder.
- Expand the troubleshooting section with actual compiler outputs and corresponding fixes (I can capture common error outputs if desired).

-- End of document
