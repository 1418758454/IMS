-- Disable captcha validation for login and the built-in registration endpoint.
-- Restart the backend after applying this script so the cached setting is reloaded.
UPDATE sys_config
SET config_value = 'false',
    update_by = 'admin',
    update_time = NOW()
WHERE config_key = 'sys.account.captchaEnabled';
