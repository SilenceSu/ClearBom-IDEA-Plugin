package core;

/**
 * Created by SilenceSu on 2017/5/19.
 * Email:silence.sx@gmail.com
 */
public class Parameters {

    private String src;
    private String[] masks = new String[]{"*.java"};
    private boolean recursively = false;
    private boolean ready = true;

    public Parameters(String src, String[] masks, boolean deep, boolean ready) {
        this.src = src;
        this.masks = masks;
        this.recursively = deep;
        this.ready = ready;
    }

    public Parameters(String src, boolean recursively) {
        this.src = src;
        this.recursively = recursively;
    }

    public Parameters(String src) {
        this.src = src;
    }

    public static class Builder {
        private String src;
        private String[] masks;
        private boolean recursively;

        public Builder folder(String src) {
            this.src = src;
            return this;
        }

        public Builder masks(String[] masks) {
            this.masks = masks;
            return this;
        }

        public Builder recursively(boolean recursively) {
            this.recursively = recursively;
            return this;
        }

        public Parameters build(boolean ready) {
            return new Parameters(src, masks, recursively, ready);
        }

    }

    public String getSrc() {
        return src;
    }

    public String[] getMasks() {
        return masks;
    }

    public boolean isRecursively() {
        return recursively;
    }

    public boolean isReady() {
        return ready;
    }
}
